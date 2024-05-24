package com.gifs.gallery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GifEntity(
    @PrimaryKey
    val id: String,
    val ratio: Float,
    val url: String,
    val downsizedUrl: String
)