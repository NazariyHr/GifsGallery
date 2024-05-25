package com.gifs.gallery.presentation.features.gifs_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.gifs.gallery.domain.model.Gif
import com.gifs.gallery.presentation.common.theme.ScarletRed
import kotlinx.coroutines.Dispatchers

@Composable
fun GifItem(
    gif: Gif,
    onRemoveGifClicked: (gifId: String) -> Unit,
    onGifClicked: (gif: Gif) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable {
                onGifClicked(gif)
            },
        contentAlignment = Alignment.TopEnd
    ) {
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
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
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
        if (imageLoaded) {
            Box(
                modifier = Modifier.clickable {
                    onRemoveGifClicked(gif.id)
                },
                contentAlignment = Alignment.TopEnd
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
        onRemoveGifClicked = {},
        onGifClicked = {}
    )
}