package com.gifs.gallery.presentation.features.gifs_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifs.gallery.domain.errors.GifsLoadingError
import com.gifs.gallery.domain.use_case.GetGifsUseCase
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
    val getGifsUseCase: GetGifsUseCase
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
                    val loadedGifs = getGifsUseCase(lastNumber = 0)
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

    fun onScrolledToTheEnd() {
        if (!_state.value.endOfListReached && !_state.value.isLoading) {
            loadMore()
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            try {
                stateValue = stateValue.copy(isLoading = true)
                val gifsCount = _state.value.gifs.count()
                val loadedGifs = getGifsUseCase(lastNumber = gifsCount)
                stateValue = stateValue.copy(
                    gifs = stateValue.gifs + loadedGifs,
                    endOfListReached = loadedGifs.isEmpty(),
                    isLoading = false
                )
            } catch (e: GifsLoadingError) {
                _errors.send(e.message ?: "Error occurred during loading")
                stateValue = stateValue.copy(isLoading = false)
            }
        }
    }
}