package com.backend.chirp.service.auth

import com.backend.chirp.domain.exception.UserAlreadyExistsException
import com.backend.chirp.domain.model.User
import com.backend.chirp.infra.database.entities.UserEntity
import com.backend.chirp.infra.database.mappers.toUser
import com.backend.chirp.infra.database.repositories.UserRepository
import com.backend.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}