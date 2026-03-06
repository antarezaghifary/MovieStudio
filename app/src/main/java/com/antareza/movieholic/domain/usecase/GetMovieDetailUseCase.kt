package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Movie {
        return movieRepository.getMovieDetail(movieId)
    }
}