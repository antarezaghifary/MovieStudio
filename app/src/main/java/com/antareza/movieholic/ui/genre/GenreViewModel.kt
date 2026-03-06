package com.antareza.movieholic.ui.genre

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antareza.movieholic.domain.usecase.GetMovieGenresUseCase
import com.antareza.movieholic.ui.genre.state.GenreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : ViewModel() {

    private val _genreState = mutableStateOf<GenreState>(GenreState.Loading)
    val genreState: State<GenreState> = _genreState

    init {
        getMovieGenres()
    }

    private fun getMovieGenres() {
        viewModelScope.launch {
            try {
                val genres = getMovieGenresUseCase()
                _genreState.value = GenreState.Success(genres)
            } catch (e: Exception) {
                _genreState.value = GenreState.Error(e.message.toString())
            }
        }
    }
}