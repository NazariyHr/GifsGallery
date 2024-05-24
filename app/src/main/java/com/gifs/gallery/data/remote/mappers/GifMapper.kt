package com.gifs.gallery.data.remote.mappers

import com.gifs.gallery.data.local.entity.GifEntity
import com.gifs.gallery.data.remote.dto.GifDto
import com.gifs.gallery.domain.model.Gif

fun GifDto.toGif(): Gif {
    return Gif(
        id = id,
        ratio = gifInfo.downsized.width.toFloat() / gifInfo.downsized.height,
        url = gifInfo.original.url,
        downsizedUrl = gifInfo.downsized.url
    )
}

fun GifDto.toGifEntity(): GifEntity {
    return GifEntity(
        id = id,
        ratio = gifInfo.downsized.width.toFloat() / gifInfo.downsized.height,
        url = gifInfo.original.url,
        downsizedUrl = gifInfo.downsized.url
    )
}