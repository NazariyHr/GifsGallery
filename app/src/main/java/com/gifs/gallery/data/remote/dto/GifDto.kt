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
    val preview: Preview
)

data class Original(
    val url: String
)

data class Preview(
    val url: String?,
    val width: Int?,
    val height: Int?
)

fun Preview.isEmpty(): Boolean {
    return url == null || width == null || height == null
}