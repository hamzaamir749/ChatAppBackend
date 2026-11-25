package com.backend.chirp.api.mappers

import com.backend.chirp.api.dto.AuthenticatedUserDto
import com.backend.chirp.api.dto.UserDto
import com.backend.chirp.domain.model.AuthenticatedUser
import com.backend.chirp.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified
    )
}