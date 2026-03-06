package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesByGenreUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(genreId: Int, page: Int): List<Movie> {
        return movieRepository.getMoviesByGenre(genreId, page)
    }
}