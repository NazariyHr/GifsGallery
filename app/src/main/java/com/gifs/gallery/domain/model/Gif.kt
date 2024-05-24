package com.gifs.gallery.domain.model

data class Gif(
    val id: String,
    val ratio: Float,
    val url: String,
    val downsizedUrl: String
)