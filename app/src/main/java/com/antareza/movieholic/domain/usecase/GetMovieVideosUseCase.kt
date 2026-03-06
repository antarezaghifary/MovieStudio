package com.antareza.movieholic.domain.usecase

import com.antareza.movieholic.domain.model.Video
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): List<Video> {
        return repository.getMovieVideos(movieId)
    }
}
