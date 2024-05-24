package com.gifs.gallery.data.local.mappers

import com.gifs.gallery.data.local.entity.GifEntity
import com.gifs.gallery.domain.model.Gif

fun GifEntity.toGif(): Gif {
    return Gif(
        id = id,
        url = url,
        downsizedUrl = downsizedUrl
    )
}