package com.antareza.movieholic.domain.repository

import com.antareza.movieholic.domain.model.Genre
import com.antareza.movieholic.domain.model.Movie

interface MovieRepository {
    suspend fun getMovieGenres(): List<Genre>
    suspend fun getMoviesByGenre(genreId: Int, page: Int): List<Movie>
    suspend fun getMovieDetail(movieId: Int): Movie
    suspend fun getMovieReviews(movieId: Int, page: Int): List<com.antareza.movieholic.domain.model.Review>
    suspend fun getMovieVideos(movieId: Int): List<com.antareza.movieholic.domain.model.Video>
    suspend fun searchMovies(query: String, page: Int): List<Movie>
}