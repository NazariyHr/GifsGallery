package com.gifs.gallery.presentation.features.gifs_list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext

@Composable
fun GifsListScreenRoot(
    viewModel: GifsListViewModel = hiltViewModel(),
) {
    val gifs by viewModel.state.collectAsStateWithLifecycle()
    GifsListScreen(gifs, viewModel.errors, viewModel::onScrolledToTheEnd)
}

@Composable
fun GifsListScreen(
    state: GifsListState,
    errorsFlow: Flow<String>,
    onScrolledToTheEnd: () -> Unit
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
                onScrolledToTheEnd()
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.weight(1f),
                columns = StaggeredGridCells.Fixed(4),
                state = gridState,
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                content = {
                    items(state.gifs, key = { it.id }) { gif ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .decoderFactory(GifDecoder.Factory())
                                .dispatcher(Dispatchers.IO)
                                .memoryCacheKey(gif.downsizedUrl)
                                .diskCacheKey(gif.downsizedUrl)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .data(gif.downsizedUrl)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(gif.ratio)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
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