package com.antareza.movieholic.data.repository

import com.antareza.movieholic.data.remote.ApiService
import com.antareza.movieholic.data.toDomain
import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.domain.model.Movie
import com.antareza.movieholic.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRepository {

    override suspend fun getMovieGenres(): List<Genre> {
        return apiService.getMovieGenres().genres.map { it.toDomain() }
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): List<Movie> {
        return apiService.getMoviesByGenre(genreId = genreId, page = page).results.map { it.toDomain() }
    }

    override suspend fun getMovieDetail(movieId: Int): Movie {
        return apiService.getMovieDetail(movieId = movieId).toDomain()
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): List<com.antareza.movieholic.domain.model.Review> {
        return apiService.getMovieReviews(movieId = movieId, page = page).results.map { it.toDomain() }
    }

    override suspend fun getMovieVideos(movieId: Int): List<com.antareza.movieholic.domain.model.Video> {
        return apiService.getMovieVideos(movieId = movieId).results.map { it.toDomain() }
    }

    override suspend fun searchMovies(query: String, page: Int): List<Movie> {
        return apiService.searchMovies(query = query, page = page).results.map { it.toDomain() }
    }
}