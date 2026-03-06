package com.antareza.movieholic.data.remote.response

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Review>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class Review(
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String
)