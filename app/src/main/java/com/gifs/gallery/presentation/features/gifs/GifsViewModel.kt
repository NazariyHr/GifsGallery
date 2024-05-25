package com.gifs.gallery.presentation.features.gifs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.use_case.GetGifsUseCase
import com.gifs.gallery.domain.use_case.GetNetworkConnectivityUseCase
import com.gifs.gallery.domain.use_case.RemoveGifUseCase
import com.gifs.gallery.domain.use_case.SearchGifsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getGifsUseCase: GetGifsUseCase,
    private val removeGifUseCase: RemoveGifUseCase,
    private val searchGifsUseCase: SearchGifsUseCase,
    getNetworkConnectivityUseCase: GetNetworkConnectivityUseCase
) : ViewModel() {

    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: GifsState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<GifsState>(STATE_KEY)!!
        }

    private val _state = savedStateHandle.getStateFlow(STATE_KEY, GifsState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _state.value)

    private val _errors = Channel<String>()
    val errors = _errors.receiveAsFlow()

    init {
        getNetworkConnectivityUseCase()
            .onEach { connected ->
                if (stateValue.needInternedToProceedLoading && connected) {
                    stateValue = stateValue.copy(
                        connectedToNetwork = connected,
                        needInternedToProceedLoading = false
                    )
                    loadMore()
                } else {
                    stateValue = stateValue.copy(
                        connectedToNetwork = connected
                    )
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            if (_state.value.gifs.isEmpty()) {
                stateValue = stateValue.copy(isLoading = true)
                try {
                    val loadedGifs = getGifsUseCase(
                        idsNotToLoad = alreadyShowedGifsId()
                    )
                    stateValue = stateValue.copy(
                        gifs = loadedGifs,
                        endOfListReached = loadedGifs.isEmpty(),
                        isLoading = false
                    )
                } catch (e: GifsLoadingError) {
                    stateValue = stateValue.copy(isLoading = false)
                    _errors.send(e.message ?: "Error occurred during loading")
                }
            }
        }
    }

    fun onAction(action: GifsAction) {
        when (action) {
            GifsAction.OnScrolledToEnd -> {
                if (!_state.value.endOfListReached && !_state.value.isLoading && !_state.value.needInternedToProceedLoading) {
                    loadMore()
                }
            }

            is GifsAction.OnRemoveGifClicked -> {
                viewModelScope.launch {
                    val newList = stateValue.gifs.filter { it.id != action.gifId }
                    stateValue = stateValue.copy(
                        gifs = newList
                    )
                    removeGifUseCase(action.gifId)
                }
            }

            is GifsAction.OnSearch -> {
                stateValue = stateValue.copy(
                    isLoading = true,
                    gifs = emptyList(),
                    displayingItems = DisplayingItems.SEARCH,
                    searchQuery = action.searchQuery
                )
                viewModelScope.launch {
                    try {
                        val loadedGifs =
                            searchGifsUseCase(
                                queryString = action.searchQuery,
                                idsNotToLoad = alreadyShowedGifsId()
                            )
                        stateValue = stateValue.copy(
                            gifs = loadedGifs,
                            endOfListReached = loadedGifs.isEmpty(),
                            isLoading = false
                        )
                    } catch (e: GifsLoadingError) {
                        stateValue = stateValue.copy(
                            isLoading = false
                        )
                        _errors.send(e.message ?: "Error occurred during loading")
                    }
                }
            }

            GifsAction.OnCloseSearchClicked -> {
                stateValue = stateValue.copy(
                    isLoading = true,
                    gifs = emptyList(),
                    displayingItems = DisplayingItems.ALL,
                    searchQuery = ""
                )
                viewModelScope.launch {
                    try {
                        val loadedGifs =
                            getGifsUseCase(
                                idsNotToLoad = alreadyShowedGifsId()
                            )
                        stateValue = stateValue.copy(
                            gifs = loadedGifs,
                            endOfListReached = loadedGifs.isEmpty(),
                            isLoading = false
                        )
                    } catch (e: GifsLoadingError) {
                        stateValue = stateValue.copy(
                            isLoading = false
                        )
                        _errors.send(e.message ?: "Error occurred during loading")
                    }
                }
            }
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            try {
                stateValue = stateValue.copy(isLoading = true)
                val loadedGifs = if (_state.value.displayingItems == DisplayingItems.SEARCH) {
                    searchGifsUseCase(
                        _state.value.searchQuery,
                        idsNotToLoad = alreadyShowedGifsId()
                    )
                } else {
                    getGifsUseCase(idsNotToLoad = alreadyShowedGifsId())
                }
                stateValue = stateValue.copy(
                    gifs = stateValue.gifs + loadedGifs,
                    endOfListReached = loadedGifs.isEmpty(),
                    isLoading = false
                )
            } catch (e: GifsLoadingError) {
                stateValue = stateValue.copy(
                    isLoading = false,
                    needInternedToProceedLoading = !_state.value.connectedToNetwork
                )
                if (_state.value.connectedToNetwork) {
                    _errors.send(e.message ?: "Error occurred during loading")
                }
            }
        }
    }

    private fun alreadyShowedGifsId(): List<String> {
        return _state.value.gifs.map { it.id }
    }
}