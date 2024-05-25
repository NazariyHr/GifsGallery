package com.gifs.gallery.presentation.features.gifs_list

sealed class GifsListScreenAction {
    data object OnScrolledToEnd : GifsListScreenAction()
    data class OnRemoveGifClicked(val gifId: String) : GifsListScreenAction()
    data class OnSearch(val searchQuery: String) : GifsListScreenAction()
    data object OnCloseSearchClicked : GifsListScreenAction()
}