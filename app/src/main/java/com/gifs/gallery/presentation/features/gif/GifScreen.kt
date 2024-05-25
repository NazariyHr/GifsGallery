package com.gifs.gallery.presentation.features.gif

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gifs.gallery.domain.model.Gif
import kotlinx.coroutines.Dispatchers

@Composable
fun GifScreen(
    gif: Gif
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .decoderFactory(GifDecoder.Factory())
            .dispatcher(Dispatchers.IO)
            .memoryCacheKey(gif.url)
            .diskCacheKey(gif.url)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .data(gif.url)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(gif.ratio),
        contentScale = ContentScale.Fit
    )
}