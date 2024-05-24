package com.gifs.gallery.domain.use_case

import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.local.mappers.toGif
import com.gifs.gallery.data.remote.GiphyApi
import com.gifs.gallery.data.remote.mappers.toGif
import com.gifs.gallery.data.remote.mappers.toGifEntity
import com.gifs.gallery.domain.model.Gif
import javax.inject.Inject

class GetTrendingGifsUseCase @Inject constructor(
    private val giphyApi: GiphyApi,
    private val gifsDatabase: GifsDatabase
) {
    suspend operator fun invoke(): List<Gif> {
        val localGifs = gifsDatabase.gifsDao.allGifs()
        if (localGifs.isNotEmpty()) return localGifs.map { it.toGif() }
        val remoteGifs = giphyApi.getTrendingGifs(1).data
        gifsDatabase.gifsDao.upsertAll(remoteGifs.map { it.toGifEntity() })
        return remoteGifs.map { it.toGif() }
    }
}