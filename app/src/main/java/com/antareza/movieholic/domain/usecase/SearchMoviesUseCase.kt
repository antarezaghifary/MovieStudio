package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int): List<Movie> {
        return repository.searchMovies(query, page)
    }
}
