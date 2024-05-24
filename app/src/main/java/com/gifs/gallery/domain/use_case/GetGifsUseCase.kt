package com.gifs.gallery.domain.use_case

import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.local.mappers.toGif
import com.gifs.gallery.data.remote.GiphyApi
import com.gifs.gallery.data.remote.dto.common.isEnd
import com.gifs.gallery.data.remote.mappers.toGifEntity
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.model.Gif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

typealias LastItemNumber = Int

class GetGifsUseCase @Inject constructor(
    private val giphyApi: GiphyApi,
    private val gifsDatabase: GifsDatabase
) {
    companion object {
        const val PER_PAGE = GiphyApi.BASE_LIMIT
    }

    @Throws(GifsLoadingError::class)
    suspend operator fun invoke(lastNumber: LastItemNumber): List<Gif> {
        return withContext(Dispatchers.IO) {
            try {
                var localGifs = gifsDatabase.gifsDao.getGifs(limit = PER_PAGE, offset = lastNumber)
                if (localGifs.isNotEmpty()) return@withContext localGifs.map { it.toGif() }

                var newGifsLoaded = false
                var lastRemoteOffset = lastNumber

                val removedGifsIds = gifsDatabase.removedGifsDao.getAll().map { it.id }

                while (!newGifsLoaded) {
                    val remoteResponse = giphyApi.getTrendingGifs(lastRemoteOffset)
                    lastRemoteOffset += PER_PAGE
                    val remoteGifs = remoteResponse.data.filter { it.id !in removedGifsIds }
                    gifsDatabase.gifsDao.upsertAll(remoteGifs.map { it.toGifEntity() })
                    localGifs = gifsDatabase.gifsDao.getGifs(limit = PER_PAGE, offset = lastNumber)
                    newGifsLoaded = localGifs.count() == PER_PAGE
                    val endOfListInRemote = remoteResponse.pagination.isEnd()
                    if (endOfListInRemote) break
                }
                return@withContext localGifs.map { it.toGif() }
            } catch (e: IOException) {
                throw GifsLoadingError(e.message)
            } catch (e: HttpException) {
                throw GifsLoadingError(e.message)
            } catch (e: Throwable) {
                throw GifsLoadingError(e.message)
            }
        }
    }
}