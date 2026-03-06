package com.antareza.movieholic.ui.detail.state

import com.antareza.movieholic.domain.model.Video

sealed class VideoState {
    object Loading : VideoState()
    data class Success(val videos: List<Video>) : VideoState()
    data class Error(val message: String) : VideoState()
}
