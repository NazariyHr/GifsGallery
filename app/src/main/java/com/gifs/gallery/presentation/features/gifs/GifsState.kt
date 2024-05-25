package com.gifs.gallery.presentation.features.gifs

import android.os.Parcelable
import com.gifs.gallery.domain.model.Gif
import kotlinx.parcelize.Parcelize

@Parcelize
data class GifsState(
    val gifs: List<Gif> = emptyList(),
    val isLoading: Boolean = false,
    val endOfListReached: Boolean = false,
    val displayingItems: DisplayingItems = DisplayingItems.ALL,
    val searchQuery: String = "",
    val connectedToNetwork: Boolean = true,
    val needInternedToProceedLoading: Boolean = false,
) : Parcelable

enum class DisplayingItems {
    ALL, SEARCH
}