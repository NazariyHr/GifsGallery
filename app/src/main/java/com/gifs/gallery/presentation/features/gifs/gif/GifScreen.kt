package com.gifs.gallery.presentation.features.gifs.gif

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gifs.gallery.domain.model.Gif
import com.gifs.gallery.presentation.features.gifs.GifsAction
import com.gifs.gallery.presentation.features.gifs.GifsListState
import com.gifs.gallery.presentation.features.gifs.GifsViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun GifScreenRoot(
    navController: NavController,
    viewModel: GifsViewModel,
    gif: Gif
) {
    val gifs by viewModel.state.collectAsStateWithLifecycle()
    GifScreen(
        state = gifs,
        gif = gif,
        onAction = viewModel::onAction,
        onBackPressed = {
            navController.navigateUp()
        }
    )
}

@Composable
fun GifScreen(
    state: GifsListState,
    gif: Gif,
    onAction: (GifsAction) -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopStart
    ) {
        if (state.gifs.isNotEmpty()) {
            val pagerState = rememberPagerState(
                pageCount = {
                    state.gifs.count()
                },
                initialPage = state.gifs.indexOfFirst { it.id == gif.id }
            )

            LaunchedEffect(pagerState, state.gifs) {
                snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
                    if (pageIndex == state.gifs.count() - 1 && !state.isLoading) {
                        onAction(GifsAction.OnScrolledToEnd)
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                if (pageIndex == state.gifs.count() - 1) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    val gifInPage = state.gifs[pageIndex]
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .decoderFactory(GifDecoder.Factory())
                            .dispatcher(Dispatchers.IO)
                            .memoryCacheKey(gifInPage.url)
                            .diskCacheKey(gifInPage.url)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .data(gifInPage.url)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clickable {
                    onBackPressed()
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
fun GifScreenPreview() {
    GifScreen(GifsListState(), Gif("", 1f, "", ""), {}, {})
}