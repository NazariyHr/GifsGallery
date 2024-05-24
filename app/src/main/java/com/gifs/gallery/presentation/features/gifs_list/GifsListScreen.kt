package com.gifs.gallery.presentation.features.gifs_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GifsListScreenRoot() {
    GifsListScreen()
}

@Composable
fun GifsListScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Gifs list"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GifsListScreenPreview() {
    GifsListScreen()
}