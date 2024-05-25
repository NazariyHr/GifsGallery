package com.gifs.gallery.presentation.features.gifs.gifs_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.gifs.gallery.R
import com.gifs.gallery.domain.model.Gif
import com.gifs.gallery.presentation.common.theme.ScarletRed
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoilApi::class)
@Composable
fun GifItem(
    gif: Gif,
    connectedToNetwork: Boolean,
    onRemoveGifClicked: (gifId: String) -> Unit,
    onGifClicked: (gif: Gif) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clickable {
                onGifClicked(gif)
            },
        contentAlignment = Alignment.TopEnd
    ) {
        var cantLoadGifCauseOfNetwork by rememberSaveable {
            mutableStateOf(false)
        }

        var imageLoaded by rememberSaveable { mutableStateOf(false) }
        val listener = object : ImageRequest.Listener {
            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                super.onSuccess(request, result)
                imageLoaded = true
            }

            override fun onStart(request: ImageRequest) {
                super.onStart(request)
                imageLoaded = false
            }

            override fun onError(request: ImageRequest, result: ErrorResult) {
                super.onError(request, result)
                if (!connectedToNetwork) {
                    cantLoadGifCauseOfNetwork = true
                }
            }
        }

        if (connectedToNetwork && cantLoadGifCauseOfNetwork) {
            cantLoadGifCauseOfNetwork = false
        }

        if (cantLoadGifCauseOfNetwork) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.no_internet),
                    contentDescription = "No internet",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .decoderFactory(GifDecoder.Factory())
                    .dispatcher(Dispatchers.IO)
                    .listener(listener)
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
        if (imageLoaded) {
            Box(
                modifier = Modifier.clickable {
                    onRemoveGifClicked(gif.id)
                    ImageLoader(context).diskCache?.remove(gif.downsizedUrl)
                    ImageLoader(context).diskCache?.remove(gif.url)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = ScarletRed,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun GifItemPreview() {
    GifItem(
        gif = Gif(
            id = "id",
            ratio = 1.7f,
            url = "",
            downsizedUrl = ""
        ),
        connectedToNetwork = true,
        onRemoveGifClicked = {},
        onGifClicked = {}
    )
}