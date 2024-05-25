package com.gifs.gallery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gifs.gallery.data.local.dao.GifDao
import com.gifs.gallery.data.local.dao.RemovedGifDao
import com.gifs.gallery.data.local.dao.SearchKeyWordDao
import com.gifs.gallery.data.local.entity.GifEntity
import com.gifs.gallery.data.local.entity.RemovedGifEntity
import com.gifs.gallery.data.local.entity.SearchKeyWordEntity

@Database(
    entities = [GifEntity::class, RemovedGifEntity::class, SearchKeyWordEntity::class],
    version = 1
)
abstract class GifsDatabase : RoomDatabase() {
    abstract val gifsDao: GifDao
    abstract val removedGifsDao: RemovedGifDao
    abstract val searchKeyWordDao: SearchKeyWordDao
}