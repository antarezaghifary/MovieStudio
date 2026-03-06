package com.antareza.movieholic.ui.movie

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.domain.usecase.GetMovieGenresUseCase
import com.antareza.movieholic.domain.usecase.GetMoviesByGenreUseCase
import com.antareza.movieholic.domain.usecase.SearchMoviesUseCase
import com.antareza.movieholic.ui.movie.state.MovieState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieState: MutableState<MovieState> = mutableStateOf(MovieState.Loading)
    val movieState: State<MovieState> = _movieState

    private val _genres = mutableStateOf<List<Genre>>(emptyList())
    val genres: State<List<Genre>> = _genres

    private val _selectedGenre = mutableStateOf<Genre?>(null)
    val selectedGenre: State<Genre?> = _selectedGenre

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _page = mutableStateOf(1)
    private val initialGenreId = savedStateHandle.get<Int>("genreId") ?: 0

    private var isLoading = false
    private var isEndReached = false

    private var searchJob: Job? = null

    init {
        fetchGenres()
        getMovies(if (initialGenreId != 0) initialGenreId else 28) // fallback to Action (28) if 0
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                _genres.value = getMovieGenresUseCase()
                val selected = _genres.value.find { it.id == initialGenreId }
                if (selected != null) {
                    _selectedGenre.value = selected
                } else if (_genres.value.isNotEmpty()) {
                    _selectedGenre.value = _genres.value.find { it.id == 28 } ?: _genres.value.first()
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _page.value = 1
            isEndReached = false
            if (query.isNotEmpty()) {
                _selectedGenre.value = null // clear genre selection when searching
                searchMovies(query)
            } else {
                // query is empty, revert to selected genre
                if (_genres.value.isNotEmpty()) {
                    val fallbackGenre = _genres.value.find { it.id == initialGenreId } ?: _genres.value.first()
                    _selectedGenre.value = fallbackGenre
                    getMovies(fallbackGenre.id)
                }
            }
        }
    }

    fun onSearchIconClicked() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchQuery.value = ""
            if (_genres.value.isNotEmpty() && _selectedGenre.value == null) {
                val fallbackGenre = _genres.value.find { it.id == initialGenreId } ?: _genres.value.first()
                onGenreSelected(fallbackGenre)
            }
        }
    }

    fun onGenreSelected(genre: Genre) {
        if (_selectedGenre.value?.id == genre.id) return
        _selectedGenre.value = genre
        _searchQuery.value = ""
        _isSearching.value = false
        _page.value = 1
        isEndReached = false
        getMovies(genre.id)
    }

    private fun getMovies(genreId: Int) {
        if (isLoading) return
        isLoading = true
        if (_page.value == 1) _movieState.value = MovieState.Loading
        viewModelScope.launch {
            try {
                val movies = getMoviesByGenreUseCase(genreId, _page.value)
                if (movies.isEmpty()) {
                    isEndReached = true
                }
                val currentState = _movieState.value
                if (_page.value == 1) {
                    _movieState.value = MovieState.Success(movies)
                } else if (currentState is MovieState.Success) {
                    val currentMovies = currentState.movies.toMutableList()
                    currentMovies.addAll(movies)
                    _movieState.value = MovieState.Success(currentMovies)
                }
                isLoading = false
            } catch (e: Exception) {
                if (_page.value == 1) {
                    _movieState.value = MovieState.Error(e.message.toString())
                }
                isLoading = false
            }
        }
    }

    private fun searchMovies(query: String) {
        if (isLoading) return
        isLoading = true
        if (_page.value == 1) _movieState.value = MovieState.Loading
        viewModelScope.launch {
            try {
                val movies = searchMoviesUseCase(query, _page.value)
                if (movies.isEmpty()) {
                    isEndReached = true
                }
                val currentState = _movieState.value
                if (_page.value == 1) {
                    _movieState.value = MovieState.Success(movies)
                } else if (currentState is MovieState.Success) {
                    val currentMovies = currentState.movies.toMutableList()
                    currentMovies.addAll(movies)
                    _movieState.value = MovieState.Success(currentMovies)
                }
                isLoading = false
            } catch (e: Exception) {
                if (_page.value == 1) {
                    _movieState.value = MovieState.Error(e.message.toString())
                }
                isLoading = false
            }
        }
    }

    fun loadMoreMovies() {
        if (!isLoading && !isEndReached) {
            _page.value++
            if (_searchQuery.value.isNotEmpty()) {
                searchMovies(_searchQuery.value)
            } else if (_selectedGenre.value != null) {
                getMovies(_selectedGenre.value!!.id)
            }
        }
    }
}