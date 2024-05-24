package com.gifs.gallery.domain.use_case

import androidx.room.withTransaction
import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.local.entity.RemovedGifEntity
import com.gifs.gallery.domain.errors.GifsLoadingError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveGifUseCase @Inject constructor(
    private val gifsDatabase: GifsDatabase
) {
    @Throws(GifsLoadingError::class)
    suspend operator fun invoke(gifId: String) {
        withContext(Dispatchers.IO) {
            gifsDatabase.withTransaction {
                gifsDatabase.gifsDao.deleteGif(gifId)
                gifsDatabase.removedGifsDao.insert(RemovedGifEntity(gifId))
            }
        }
    }
}