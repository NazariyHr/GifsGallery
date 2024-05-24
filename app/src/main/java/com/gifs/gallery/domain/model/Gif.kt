package com.gifs.gallery.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gif(
    val id: String,
    val ratio: Float,
    val url: String,
    val downsizedUrl: String
) : Parcelable