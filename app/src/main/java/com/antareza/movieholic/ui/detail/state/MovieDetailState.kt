package com.antareza.movieholic.ui.detail.state

import com.antareza.movieholic.domain.model.Movie

sealed class MovieDetailState {
    object Loading : MovieDetailState()
    data class Success(val movie: Movie) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}