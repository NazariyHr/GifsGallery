package com.gifs.gallery.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = GifEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("gifId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class SearchKeyWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val word: String,
    @ColumnInfo(index = true)
    val gifId: String
)