package com.gifs.gallery.presentation.features.gifs

import android.os.Parcelable
import com.gifs.gallery.domain.model.Gif
import kotlinx.parcelize.Parcelize

@Parcelize
data class GifsListState(
    val gifs: List<Gif> = emptyList(),
    val isLoading: Boolean = false,
    val endOfListReached: Boolean = false,
    val displayingItems: DisplayingItems = DisplayingItems.ALL,
    val searchQuery: String = ""
) : Parcelable

enum class DisplayingItems {
    ALL, SEARCH
}