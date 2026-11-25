package com.backend.chirp.api.exception_handling

import com.backend.chirp.domain.exception.UserAlreadyExistsException
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onUserAlreadyExists(
        e: UserAlreadyExistsException
    ) = mapOf("code" to "User Exists", "message" to e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidationException(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid Value"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("code" to "VALIDATION_ERROR", "error" to errors))
    }
}