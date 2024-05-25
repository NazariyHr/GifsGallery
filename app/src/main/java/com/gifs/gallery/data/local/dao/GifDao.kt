package com.gifs.gallery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gifs.gallery.data.local.entity.GifEntity

@Dao
interface GifDao {
    @Insert
    suspend fun insertAll(gifs: List<GifEntity>)

    @Query("select * from gifentity limit :limit offset :offset")
    suspend fun getGifs(limit: Int, offset: Int): List<GifEntity>

    @Query("delete from gifentity where id = :id")
    suspend fun deleteGif(id: String)

    @Query("SELECT * FROM gifentity where id in (select gifId from searchkeywordentity where word in (:keywords)) limit :limit offset :offset")
    suspend fun search(keywords: List<String>, limit: Int, offset: Int): List<GifEntity>

    @Query("select id from gifentity where id in (:ids)")
    suspend fun getIdsThatAlreadyExists(ids: List<String>): List<String>
}