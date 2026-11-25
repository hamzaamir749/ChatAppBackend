package com.backend.chirp.service.auth

import com.backend.chirp.domain.exception.InvalidCredentialsException
import com.backend.chirp.domain.exception.InvalidTokenException
import com.backend.chirp.domain.exception.UserAlreadyExistsException
import com.backend.chirp.domain.exception.UserNotFoundException
import com.backend.chirp.domain.model.AuthenticatedUser
import com.backend.chirp.domain.model.User
import com.backend.chirp.domain.model.UserId
import com.backend.chirp.infra.database.entities.RefreshTokenEntity
import com.backend.chirp.infra.database.entities.UserEntity
import com.backend.chirp.infra.database.mappers.toUser
import com.backend.chirp.infra.database.repositories.RefreshTokenRepository
import com.backend.chirp.infra.database.repositories.UserRepository
import com.backend.chirp.infra.security.PasswordEncoder
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun register(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailAndUsername(email = email.trim(), username = username.trim())
        if (user != null) {
            throw UserAlreadyExistsException()
        }

        val saveUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password)
            )
        ).toUser()
        return saveUser
    }

    fun login(email: String, password: String): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim()) ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(rawPassword = password, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }
        //TODO : Check for verified email
        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(
                userId = userId,
                token = refreshToken
            )
            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun logout(refreshToken: String) {
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val hashed = hashToken(refreshToken)

        refreshTokenRepository.deleteByUserIdAndHashedToken(
            userId = userId,
            hashedToken = hashed
        )
    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw InvalidTokenException(
                message = "Invalid refresh token"
            )
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        val hashed = hashToken(refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            ) ?: throw InvalidTokenException("Invalid refresh token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )

            val newAccessToken = jwtService.generateAccessToken(userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, newRefreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        } ?: throw UserNotFoundException()
    }


    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashed = hashToken(token)
        val expireMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expireMs)
        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}