package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): List<Genre> {
        return movieRepository.getMovieGenres()
    }
}