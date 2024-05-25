package com.gifs.gallery.domain.use_case

import androidx.room.withTransaction
import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.local.entity.SearchKeyWordEntity
import com.gifs.gallery.data.local.mappers.toGif
import com.gifs.gallery.data.remote.GiphyApi
import com.gifs.gallery.data.remote.dto.common.isEnd
import com.gifs.gallery.data.remote.dto.isEmpty
import com.gifs.gallery.data.remote.mappers.toGifEntity
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.model.Gif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchGifsUseCase @Inject constructor(
    private val giphyApi: GiphyApi,
    private val gifsDatabase: GifsDatabase
) {
    companion object {
        const val PER_PAGE = GiphyApi.BASE_LIMIT
    }

    @Throws(GifsLoadingError::class)
    suspend operator fun invoke(
        queryString: String,
        lastNumber: LastItemNumber,
        idsNotToLoad: List<String>
    ): List<Gif> {
        return withContext(Dispatchers.IO) {
            try {
                val keywords = queryString.split(" ")
                val loadedGifs =
                    gifsDatabase.gifsDao.search(
                        keywords = keywords,
                        limit = PER_PAGE,
                        offset = lastNumber
                    )
                        .filter { it.id !in idsNotToLoad }
                        .toMutableList()

                if (loadedGifs.isEmpty()) {
                    var newGifsLoaded = false
                    var lastRemoteOffset = lastNumber

                    val removedGifsIds = gifsDatabase.removedGifsDao.getAll().map { it.id }

                    while (!newGifsLoaded) {
                        val remoteResponse =
                            giphyApi.searchGifs(searchQuery = queryString, lastRemoteOffset)
                        lastRemoteOffset += PER_PAGE
                        val idsAlreadyInDb =
                            gifsDatabase.gifsDao.getIdsThatAlreadyExists(remoteResponse.data.map { it.id })
                        val remoteGifs =
                            remoteResponse
                                .data
                                .filter { !it.gifInfo.preview.isEmpty() && it.id !in (removedGifsIds + idsAlreadyInDb + idsNotToLoad) }
                                .distinctBy { it.id }

                        gifsDatabase.withTransaction {
                            gifsDatabase.gifsDao.insertAll(remoteGifs.map { it.toGifEntity() }
                                .filterNotNull())
                            val wordsToAdd = mutableListOf<SearchKeyWordEntity>()
                            remoteGifs.forEach { gif ->
                                wordsToAdd.addAll(
                                    keywords.map { word ->
                                        SearchKeyWordEntity(word = word, gifId = gif.id)
                                    }
                                )
                            }
                            gifsDatabase.searchKeyWordDao.insertAll(wordsToAdd)
                        }

                        // here we are adding extra elements to fill list up to [PER_PAGE] amount
                        val amountToAdd = PER_PAGE - loadedGifs.count()
                        remoteGifs.forEachIndexed { index, gifDto ->
                            if (index == amountToAdd) return@forEachIndexed
                            loadedGifs.add(gifDto.toGifEntity()!!)
                        }

                        newGifsLoaded = loadedGifs.count() >= PER_PAGE
                        val endOfListInRemote = remoteResponse.pagination.isEnd()
                        if (endOfListInRemote) break
                    }
                }
                return@withContext loadedGifs.map { it.toGif() }
            } catch (e: IOException) {
                throw GifsLoadingError(e.message)
            } catch (e: HttpException) {
                throw GifsLoadingError(e.message)
            }
        }
    }
}