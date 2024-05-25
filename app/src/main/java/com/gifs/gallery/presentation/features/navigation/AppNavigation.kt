package com.gifs.gallery.presentation.features.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gifs.gallery.presentation.features.gifs.GifsViewModel
import com.gifs.gallery.presentation.features.gifs.gif.GifScreenRoot
import com.gifs.gallery.presentation.features.gifs.gifs_list.GifsListScreenRoot
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.GifsListScreen) {
        composable<Screen.GifsListScreen> {
            GifsListScreenRoot(navController, sharedViewModel<GifsViewModel>())
        }
        composable<Screen.GifScreen>(
            typeMap = mapOf(
                typeOf<Screen.GifScreen>() to parcelableType<Screen.GifScreen>()
            )
        ) {
            val gif = it.toRoute<Screen.GifScreen>().toGif()
            val vm = sharedViewModel<GifsViewModel>()
            GifScreenRoot(navController, vm, gif)
        }
    }
}

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)
}

@Composable
inline fun <reified T : ViewModel> sharedViewModel(): T {
    return hiltViewModel(LocalContext.current as ComponentActivity)
}