package com.gifs.gallery.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GifDto(
    val id: String,
    @SerializedName("images")
    val gifInfo: GifInfo
)

data class GifInfo(
    val original: Original,
    @SerializedName("preview_gif")
    val downsized: Downsized
)

data class Original(
    val url: String
)

data class Downsized(
    val url: String,
    val width: Int,
    val height: Int
)