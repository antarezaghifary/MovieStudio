package com.antareza.movieholic.data

import com.antareza.movieholic.data.remote.response.Genre as DataGenre
import com.antareza.movieholic.domain.model.Genre as DomainGenre

fun DataGenre.toDomain(): DomainGenre {
    return DomainGenre(
        id = id,
        name = name
    )
}