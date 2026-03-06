package com.antareza.movieholic.ui.movie.state

import com.antareza.movieholic.domain.model.Movie

sealed class MovieState {
    object Loading : MovieState()
    data class Success(val movies: List<Movie>) : MovieState()
    data class Error(val message: String) : MovieState()
}