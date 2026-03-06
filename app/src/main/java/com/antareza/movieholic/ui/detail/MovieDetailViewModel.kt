package com.antareza.movieholic.ui.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antareza.movieholic.domain.usecase.GetMovieDetailUseCase
import com.antareza.movieholic.domain.usecase.GetMovieReviewsUseCase
import com.antareza.movieholic.domain.usecase.GetMovieVideosUseCase
import com.antareza.movieholic.ui.detail.state.MovieDetailState
import com.antareza.movieholic.ui.detail.state.ReviewState
import com.antareza.movieholic.ui.detail.state.VideoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieDetailState: MutableState<MovieDetailState> = mutableStateOf(MovieDetailState.Loading)
    val movieDetailState: State<MovieDetailState> = _movieDetailState

    private val _reviewState: MutableState<ReviewState> = mutableStateOf(ReviewState.Loading)
    val reviewState: State<ReviewState> = _reviewState

    private val _videoState: MutableState<VideoState> = mutableStateOf(VideoState.Loading)
    val videoState: State<VideoState> = _videoState

    private val movieId = savedStateHandle.get<Int>("movieId") ?: 0

    private val _reviewPage = mutableStateOf(1)
    private var isReviewLoading = false
    private var isReviewEndReached = false

    init {
        getMovieDetail(movieId)
        getMovieVideos(movieId)
        getMovieReviews(movieId)
    }

    private fun getMovieDetail(movieId: Int) {
        viewModelScope.launch {
            try {
                val movie = getMovieDetailUseCase(movieId)
                _movieDetailState.value = MovieDetailState.Success(movie)
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(e.message.toString())
            }
        }
    }

    private fun getMovieVideos(movieId: Int) {
        viewModelScope.launch {
            try {
                val videos = getMovieVideosUseCase(movieId)
                _videoState.value = VideoState.Success(videos)
            } catch (e: Exception) {
                _videoState.value = VideoState.Error(e.message.toString())
            }
        }
    }

    private fun getMovieReviews(movieId: Int) {
        if (isReviewLoading || isReviewEndReached) return
        isReviewLoading = true
        viewModelScope.launch {
            try {
                val reviews = getMovieReviewsUseCase(movieId, _reviewPage.value)
                if (reviews.isEmpty()) {
                    isReviewEndReached = true
                }
                
                val currentState = _reviewState.value
                if (_reviewPage.value == 1) {
                    _reviewState.value = ReviewState.Success(reviews)
                } else if (currentState is ReviewState.Success) {
                    val currentReviews = currentState.reviews.toMutableList()
                    currentReviews.addAll(reviews)
                    _reviewState.value = ReviewState.Success(currentReviews)
                }
                isReviewLoading = false
            } catch (e: Exception) {
                if (_reviewPage.value == 1) {
                    _reviewState.value = ReviewState.Error(e.message.toString())
                }
                isReviewLoading = false
            }
        }
    }

    fun loadMoreReviews() {
        if (!isReviewLoading && !isReviewEndReached) {
            _reviewPage.value++
            getMovieReviews(movieId)
        }
    }
}