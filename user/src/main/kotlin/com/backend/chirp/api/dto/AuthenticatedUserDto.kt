package com.backend.chirp.api.dto

import com.backend.chirp.domain.model.User

data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)
