package com.gifs.gallery.presentation.features.gifs

sealed class GifsAction {
    data object OnScrolledToEnd : GifsAction()
    data class OnRemoveGifClicked(val gifId: String) : GifsAction()
    data class OnSearch(val searchQuery: String) : GifsAction()
    data object OnCloseSearchClicked : GifsAction()
}