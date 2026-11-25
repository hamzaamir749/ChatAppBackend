package com.backend.chirp.infra.database.mappers

import com.backend.chirp.domain.model.User
import com.backend.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasEmailVerified = hasVerifiedEmail
    )
}