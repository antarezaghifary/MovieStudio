package com.antareza.movieholic.ui.review

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antareza.movieholic.domain.usecase.GetMovieReviewsUseCase
import com.antareza.movieholic.ui.detail.state.ReviewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _reviewState: MutableState<ReviewState> = mutableStateOf(ReviewState.Loading)
    val reviewState: State<ReviewState> = _reviewState

    private val movieId = savedStateHandle.get<Int>("movieId") ?: 0

    private val _reviewPage = mutableStateOf(1)
    val page: Int get() = _reviewPage.value
    var isLoading = false
        private set
    private var isEndReached = false

    init {
        getMovieReviews(movieId)
    }

    private fun getMovieReviews(movieId: Int) {
        if (isLoading || isEndReached) return
        isLoading = true
        if (_reviewPage.value == 1) _reviewState.value = ReviewState.Loading
        
        viewModelScope.launch {
            try {
                val reviews = getMovieReviewsUseCase(movieId, _reviewPage.value)
                if (reviews.isEmpty()) {
                    isEndReached = true
                }
                
                val currentState = _reviewState.value
                if (_reviewPage.value == 1) {
                    _reviewState.value = ReviewState.Success(reviews)
                } else if (currentState is ReviewState.Success) {
                    val currentReviews = currentState.reviews.toMutableList()
                    currentReviews.addAll(reviews)
                    _reviewState.value = ReviewState.Success(currentReviews)
                }
                isLoading = false
            } catch (e: Exception) {
                if (_reviewPage.value == 1) {
                    _reviewState.value = ReviewState.Error(e.message.toString())
                }
                isLoading = false
            }
        }
    }

    fun loadMoreReviews() {
        if (!isLoading && !isEndReached) {
            _reviewPage.value++
            getMovieReviews(movieId)
        }
    }
}
