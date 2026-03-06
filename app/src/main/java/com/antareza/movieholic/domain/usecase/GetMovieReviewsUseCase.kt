package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Review
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieReviewsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int, page: Int): List<Review> {
        return repository.getMovieReviews(movieId, page)
    }
}
