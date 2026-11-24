package com.backend.user.domain.model

import java.util.UUID

typealias UserId = UUID


data class User(
    val id: UserId
)
