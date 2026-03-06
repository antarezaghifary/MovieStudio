package com.antareza.movieholic.data

import com.antareza.movieholic.data.remote.response.Movie as MovieResponse
import com.antareza.movieholic.domain.model.Movie as MovieDomain
import com.antareza.movieholic.data.remote.response.Review as ReviewDto
import com.antareza.movieholic.domain.model.Review as ReviewDomain
import com.antareza.movieholic.data.remote.response.VideoDto
import com.antareza.movieholic.domain.model.Video as VideoDomain

fun MovieResponse.toDomain(): MovieDomain {
    return MovieDomain(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath ?: "",
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage
    )
}

fun ReviewDto.toDomain(): ReviewDomain {
    return ReviewDomain(
        id = id,
        author = author,
        content = content,
        url = url
    )
}

fun VideoDto.toDomain(): VideoDomain {
    return VideoDomain(
        key = key,
        site = site,
        type = type
    )
}