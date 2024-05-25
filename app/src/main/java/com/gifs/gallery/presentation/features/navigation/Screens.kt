package com.gifs.gallery.presentation.features.navigation

import android.os.Parcelable
import com.gifs.gallery.domain.model.Gif
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object GifsListScreen

    @Serializable
    @Parcelize
    data class GifScreen(
        val id: String,
        val ratio: Float,
        val url: String,
        val downsizedUrl: String
    ) : Parcelable
}

fun Screen.GifScreen.toGif(): Gif {
    return Gif(
        id,
        ratio,
        url,
        downsizedUrl
    )
}

fun Gif.toGifScreen(): Screen.GifScreen {
    return Screen.GifScreen(
        id,
        ratio,
        url,
        downsizedUrl
    )
}
