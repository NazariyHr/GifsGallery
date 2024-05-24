package com.gifs.gallery.presentation.features.gifs_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gifs.gallery.domain.model.Gif
import com.gifs.gallery.domain.use_case.GetTrendingGifsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GifsListViewModel @Inject constructor(
    val getTrendingGifsUseCase: GetTrendingGifsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow((listOf<Gif>()))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _state.update {
                    getTrendingGifsUseCase()
                }
            }
        }
    }
}