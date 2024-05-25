package com.gifs.gallery.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GifWithSearchKeywords(
    @Embedded
    val gif: GifEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gifId"
    )
    val words: List<SearchKeyWordEntity>
)