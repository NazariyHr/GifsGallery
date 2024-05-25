package com.gifs.gallery.presentation.features.gifs_list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.gifs.gallery.presentation.features.gifs_list.components.GifItem
import com.gifs.gallery.presentation.features.gifs_list.components.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext

@Composable
fun GifsListScreenRoot(
    viewModel: GifsListViewModel = hiltViewModel(),
) {
    val gifs by viewModel.state.collectAsStateWithLifecycle()
    GifsListScreen(
        gifs,
        viewModel.errors,
        viewModel::onAction
    )
}

@Composable
fun GifsListScreen(
    state: GifsListState,
    errorsFlow: Flow<String>,
    onAction: (GifsListScreenAction) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(errorsFlow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                errorsFlow.collect { message ->
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState()
        val isScrolledToTheEnd by remember {
            derivedStateOf {
                val layoutInfo = gridState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (layoutInfo.visibleItemsInfo.isEmpty()) {
                    false
                } else {
                    val lastVisibleItem = visibleItemsInfo.last()
                    lastVisibleItem.index + 1 == layoutInfo.totalItemsCount - 1
                }
            }
        }
        if (isScrolledToTheEnd && !state.isLoading) {
            LaunchedEffect(Unit) {
                onAction(GifsListScreenAction.OnScrolledToEnd)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Search(
                onSearchClicked = {
                    onAction(GifsListScreenAction.OnSearch(it))
                },
                onCloseClicked = {
                    onAction(GifsListScreenAction.OnCloseSearchClicked)
                }
            )
            LazyVerticalStaggeredGrid(
                modifier = Modifier.weight(1f),
                columns = StaggeredGridCells.Fixed(4),
                state = gridState,
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                content = {
                    items(state.gifs, key = { it.id }) { gif ->
                        GifItem(gif = gif, onRemoveGifClicked = {
                            onAction(GifsListScreenAction.OnRemoveGifClicked(it))
                        })
                    }
                    item {
                        if (state.isLoading) {
                            Box(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GifsListScreenPreview() {
    GifsListScreen(GifsListState(), emptyFlow(), {})
}