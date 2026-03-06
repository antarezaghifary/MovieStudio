package com.antareza.movieholic.data.remote.response

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<VideoDto>
)

data class VideoDto(
    @SerializedName("key")
    val key: String,
    @SerializedName("site")
    val site: String,
    @SerializedName("type")
    val type: String
)
