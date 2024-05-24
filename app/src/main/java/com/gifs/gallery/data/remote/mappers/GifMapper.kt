package com.gifs.gallery.data.remote.mappers

import com.gifs.gallery.data.remote.dto.GifDto
import com.gifs.gallery.domain.model.Gif

fun GifDto.toGif(): Gif {
    return Gif(
        id = id,
        url = gifInfo.original.url,
        downsizedUrl = gifInfo.downsized.url
    )
}