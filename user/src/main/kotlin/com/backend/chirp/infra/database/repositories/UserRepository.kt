package com.backend.chirp.infra.database.repositories


import com.backend.chirp.domain.model.UserId
import com.backend.chirp.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailAndUsername(email: String, username: String): UserEntity?
}