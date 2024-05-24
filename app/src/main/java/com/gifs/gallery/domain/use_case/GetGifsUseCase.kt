package com.gifs.gallery.domain.use_case

import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.local.mappers.toGif
import com.gifs.gallery.data.remote.GiphyApi
import com.gifs.gallery.data.remote.dto.common.isEnd
import com.gifs.gallery.data.remote.mappers.toGifEntity
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.model.Gif
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

typealias LastItemNumber = Int

class GetGifsUseCase @Inject constructor(
    private val giphyApi: GiphyApi,
    private val gifsDatabase: GifsDatabase
) {
    @Throws(GifsLoadingError::class)
    suspend operator fun invoke(lastNumber: LastItemNumber): List<Gif> {
        try {
            var localGifs = gifsDatabase.gifsDao.getGifs(offset = lastNumber)
            if (localGifs.isNotEmpty()) return localGifs.map { it.toGif() }

            var newGifsLoaded = false
            while (!newGifsLoaded) {
                val remoteResponse = giphyApi.getTrendingGifs(lastNumber)
                val remoteGifs = remoteResponse.data
                gifsDatabase.gifsDao.upsertAll(remoteGifs.map { it.toGifEntity() })
                localGifs = gifsDatabase.gifsDao.getGifs(offset = lastNumber)
                newGifsLoaded = localGifs.isNotEmpty()
                val endOfListInRemote = remoteResponse.pagination.isEnd()
                if (endOfListInRemote) break
            }
            return localGifs.map { it.toGif() }
        } catch (e: IOException) {
            throw GifsLoadingError(e.message)
        } catch (e: HttpException) {
            throw GifsLoadingError(e.message)
        }
    }
}