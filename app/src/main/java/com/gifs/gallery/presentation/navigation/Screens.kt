package com.gifs.gallery.presentation.navigation

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
        val id: String
    ) : Parcelable
}

fun Gif.toGifScreen(): Screen.GifScreen {
    return Screen.GifScreen(
        id
    )
}
