package com.gifs.gallery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gifs.gallery.data.local.entity.RemovedGifEntity

@Dao
interface RemovedGifDao {
    @Insert
    suspend fun insert(gif: RemovedGifEntity)

    @Query("select * from removedgifentity")
    fun getAll(): List<RemovedGifEntity>
}