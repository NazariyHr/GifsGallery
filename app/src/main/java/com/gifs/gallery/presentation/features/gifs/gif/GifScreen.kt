package com.gifs.gallery.presentation.features.gifs.gif

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.gifs.gallery.domain.model.Gif
import com.gifs.gallery.presentation.common.connectionToNetworkState
import com.gifs.gallery.presentation.features.gifs.GifsAction
import com.gifs.gallery.presentation.features.gifs.GifsState
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
    state: GifsState,
    gif: Gif,
    onAction: (GifsAction) -> Unit,
    onBackPressed: () -> Unit
) {
    val connectedToNetwork by connectionToNetworkState()
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
                    if (pageIndex == state.gifs.count() - 1) {
                        onAction(GifsAction.OnScrolledToEnd)
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                if (pageIndex == state.gifs.count() - 1 && !state.needInternedToProceedLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    val gifInPage = state.gifs[pageIndex]
                    var urlToLoad by rememberSaveable {
                        mutableStateOf(gifInPage.url)
                    }
                    var cantLoadGifCauseOfNetwork by rememberSaveable {
                        mutableStateOf(false)
                    }

                    val listener = object : ImageRequest.Listener {
                        override fun onError(request: ImageRequest, result: ErrorResult) {
                            super.onError(request, result)
                            if (urlToLoad == gifInPage.url && !connectedToNetwork) {
                                urlToLoad = gifInPage.downsizedUrl
                            } else if (urlToLoad == gifInPage.downsizedUrl && !connectedToNetwork) {
                                cantLoadGifCauseOfNetwork = true
                            }
                        }
                    }

                    LaunchedEffect(connectedToNetwork, cantLoadGifCauseOfNetwork) {
                        if (connectedToNetwork && cantLoadGifCauseOfNetwork) {
                            urlToLoad = gifInPage.url
                            cantLoadGifCauseOfNetwork = false
                        }
                    }

                    if (cantLoadGifCauseOfNetwork) {
                        Text(
                            text = "No internet connection to load this gif.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .decoderFactory(GifDecoder.Factory())
                            .dispatcher(Dispatchers.IO)
                            .listener(listener)
                            .memoryCacheKey(urlToLoad)
                            .diskCacheKey(urlToLoad)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .data(urlToLoad)
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
    GifScreen(GifsState(), Gif("", 1f, "", ""), {}, {})
}