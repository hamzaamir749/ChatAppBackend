package com.backend.chirp.domain.exception

class UserNotFoundException(
) : RuntimeException("User not found")