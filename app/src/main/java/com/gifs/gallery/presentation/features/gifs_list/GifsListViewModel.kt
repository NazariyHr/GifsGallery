package com.gifs.gallery.presentation.features.gifs_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.use_case.GetGifsUseCase
import com.gifs.gallery.domain.use_case.RemoveGifUseCase
import com.gifs.gallery.domain.use_case.SearchGifsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifsListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getGifsUseCase: GetGifsUseCase,
    private val removeGifUseCase: RemoveGifUseCase,
    private val searchGifsUseCase: SearchGifsUseCase
) : ViewModel() {

    companion object {
        const val STATE_KEY = "state"
    }

    private var stateValue: GifsListState
        set(value) {
            savedStateHandle[STATE_KEY] = value
        }
        get() {
            return savedStateHandle.get<GifsListState>(STATE_KEY)!!
        }

    private val _state = savedStateHandle.getStateFlow(STATE_KEY, GifsListState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), _state.value)

    private val _errors = Channel<String>()
    val errors = _errors.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (_state.value.gifs.isEmpty()) {
                stateValue = stateValue.copy(isLoading = true)
                stateValue = try {
                    val loadedGifs = getGifsUseCase(
                        lastNumber = 0,
                        idsNotToLoad = alreadyShowedGifsId()
                    )
                    stateValue.copy(
                        gifs = loadedGifs,
                        endOfListReached = loadedGifs.isEmpty(),
                        isLoading = false
                    )
                } catch (e: GifsLoadingError) {
                    _errors.send(e.message ?: "Error occurred during loading")
                    stateValue.copy(isLoading = false)
                }
            }
        }
    }

    fun onAction(action: GifsListScreenAction) {
        when (action) {
            GifsListScreenAction.OnScrolledToEnd -> {
                if (!_state.value.endOfListReached && !_state.value.isLoading) {
                    loadMore()
                }
            }

            is GifsListScreenAction.OnRemoveGifClicked -> {
                viewModelScope.launch {
                    val newList = stateValue.gifs.filter { it.id != action.gifId }
                    stateValue = stateValue.copy(
                        gifs = newList
                    )
                    removeGifUseCase(action.gifId)
                }
            }

            is GifsListScreenAction.OnSearch -> {
                stateValue = stateValue.copy(
                    isLoading = true,
                    gifs = emptyList(),
                    displayingItems = DisplayingItems.SEARCH,
                    searchQuery = action.searchQuery
                )
                viewModelScope.launch {
                    stateValue = try {
                        val loadedGifs =
                            searchGifsUseCase(
                                queryString = action.searchQuery,
                                lastNumber = 0,
                                idsNotToLoad = alreadyShowedGifsId()
                            )
                        stateValue.copy(
                            gifs = loadedGifs,
                            endOfListReached = loadedGifs.isEmpty(),
                            isLoading = false
                        )
                    } catch (e: GifsLoadingError) {
                        _errors.send(e.message ?: "Error occurred during loading")
                        stateValue.copy(isLoading = false)
                    }
                }
            }

            GifsListScreenAction.OnCloseSearchClicked -> {
                stateValue = stateValue.copy(
                    isLoading = true,
                    gifs = emptyList(),
                    displayingItems = DisplayingItems.ALL,
                    searchQuery = ""
                )
                viewModelScope.launch {
                    stateValue = try {
                        val loadedGifs =
                            getGifsUseCase(
                                lastNumber = 0,
                                idsNotToLoad = alreadyShowedGifsId()
                            )
                        stateValue.copy(
                            gifs = loadedGifs,
                            endOfListReached = loadedGifs.isEmpty(),
                            isLoading = false
                        )
                    } catch (e: GifsLoadingError) {
                        _errors.send(e.message ?: "Error occurred during loading")
                        stateValue.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            try {
                stateValue = stateValue.copy(isLoading = true)
                val gifsCount = _state.value.gifs.count()
                val loadedGifs = if (_state.value.displayingItems == DisplayingItems.SEARCH) {
                    searchGifsUseCase(
                        _state.value.searchQuery,
                        lastNumber = gifsCount,
                        idsNotToLoad = alreadyShowedGifsId()
                    )
                } else {
                    getGifsUseCase(lastNumber = gifsCount, idsNotToLoad = alreadyShowedGifsId())
                }
                stateValue = stateValue.copy(
                    gifs = stateValue.gifs + loadedGifs,
                    endOfListReached = loadedGifs.isEmpty(),
                    isLoading = false
                )
            } catch (e: GifsLoadingError) {
                _errors.send(e.message ?: "Error occurred during loading")
                stateValue = stateValue.copy(
                    isLoading = false,
                    endOfListReached = true
                )
            }
        }
    }

    private fun alreadyShowedGifsId(): List<String> {
        return _state.value.gifs.map { it.id }
    }
}