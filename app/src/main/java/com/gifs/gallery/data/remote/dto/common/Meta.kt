package com.gifs.gallery.data.remote.dto.common

import com.google.gson.annotations.SerializedName

data class Meta(
    val status: Int,
    @SerializedName("msg")
    val message: String
)