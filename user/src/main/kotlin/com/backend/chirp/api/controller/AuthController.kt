package com.backend.chirp.api.controller

import com.backend.chirp.api.dto.AuthenticatedUserDto
import com.backend.chirp.api.dto.LoginRequest
import com.backend.chirp.api.dto.RefreshRequest
import com.backend.chirp.api.dto.RegisterRequest
import com.backend.chirp.api.dto.UserDto
import com.backend.chirp.api.mappers.toAuthenticatedUserDto
import com.backend.chirp.api.mappers.toUserDto
import com.backend.chirp.service.auth.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody body: RegisterRequest): UserDto {
        return authService.register(
            email = body.email,
            username = body.username,
            password = body.password,
        ).toUserDto()
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: LoginRequest): AuthenticatedUserDto {
        return authService.login(
            email = body.email,
            password = body.password
        ).toAuthenticatedUserDto()
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody body: RefreshRequest): AuthenticatedUserDto {
        return authService.refresh(refreshToken = body.refreshToken).toAuthenticatedUserDto()
    }

    @PostMapping("/logout")
    fun logout(@RequestBody body: RefreshRequest) {
        return authService.logout(
            refreshToken = body.refreshToken
        )
    }

}