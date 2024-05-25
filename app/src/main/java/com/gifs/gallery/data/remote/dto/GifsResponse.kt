package com.gifs.gallery.data.remote.dto

import com.gifs.gallery.data.remote.dto.common.Meta
import com.gifs.gallery.data.remote.dto.common.Pagination

data class GifsResponse(
    val data: List<GifDto>,
    val meta: Meta,
    val pagination: Pagination
)