package com.gifs.gallery.data.remote.mappers

import com.gifs.gallery.data.local.entity.GifEntity
import com.gifs.gallery.data.remote.dto.GifDto

fun GifDto.toGifEntity(): GifEntity? {
    if (gifInfo.preview.url == null || gifInfo.preview.width == null || gifInfo.preview.height == null) return null
    return GifEntity(
        id = id,
        ratio = gifInfo.preview.width.toFloat() / gifInfo.preview.height,
        url = gifInfo.original.url,
        downsizedUrl = gifInfo.preview.url
    )
}