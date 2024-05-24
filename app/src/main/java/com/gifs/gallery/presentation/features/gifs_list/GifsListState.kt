package com.gifs.gallery.presentation.features.gifs_list

import android.os.Parcelable
import com.gifs.gallery.domain.model.Gif
import kotlinx.parcelize.Parcelize

@Parcelize
data class GifsListState(
    val gifs: List<Gif> = emptyList(),
    val isLoading: Boolean = false,
    val endOfListReached: Boolean = false
) : Parcelable