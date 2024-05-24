package com.gifs.gallery.presentation.features.gifs_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gifs.gallery.domain.model.Gif
import kotlinx.coroutines.Dispatchers

@Composable
fun GifsListScreenRoot(
    viewModel: GifsListViewModel = hiltViewModel()
) {
    val gifs by viewModel.state.collectAsStateWithLifecycle()
    GifsListScreen(gifs)
}

@Composable
fun GifsListScreen(
    gifs: List<Gif>
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            content = {
                items(gifs, key = { it.downsizedUrl }) { item ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .decoderFactory(GifDecoder.Factory())
                            .dispatcher(Dispatchers.IO)
                            .memoryCacheKey(item.downsizedUrl)
                            .diskCacheKey(item.downsizedUrl)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .data(item.downsizedUrl)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(item.ratio)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GifsListScreenPreview() {
    GifsListScreen(listOf())
}