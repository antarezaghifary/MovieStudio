package com.antareza.movieholic.ui.genre.state

import com.antareza.movieholic.domain.model.Genre

sealed class GenreState {
    object Loading : GenreState()
    data class Success(val genres: List<Genre>) : GenreState()
    data class Error(val message: String) : GenreState()
}