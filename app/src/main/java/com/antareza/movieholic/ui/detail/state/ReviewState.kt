package com.antareza.movieholic.ui.detail.state

import com.antareza.movieholic.domain.model.Review

sealed class ReviewState {
    object Loading : ReviewState()
    data class Success(val reviews: List<Review>) : ReviewState()
    data class Error(val message: String) : ReviewState()
}
