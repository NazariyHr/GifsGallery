package com.gifs.gallery.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gifs.gallery.data.local.entity.GifEntity

@Dao
interface GifDao {
    @Upsert
    suspend fun upsertAll(gifs: List<GifEntity>)

    @Query("select * from gifentity")
    fun allGifsPagingSource(): PagingSource<Int, GifEntity>

    @Query("delete from gifentity where id = :id")
    suspend fun deleteGif(id: String)
}