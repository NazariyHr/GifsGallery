package com.gifs.gallery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemovedGifEntity(
    @PrimaryKey
    val id: String,
)