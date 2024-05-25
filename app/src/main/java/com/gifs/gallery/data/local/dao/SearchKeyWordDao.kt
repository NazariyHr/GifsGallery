package com.gifs.gallery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.gifs.gallery.data.local.entity.SearchKeyWordEntity

@Dao
interface SearchKeyWordDao {
    @Insert
    suspend fun insertAll(gifs: List<SearchKeyWordEntity>)
}