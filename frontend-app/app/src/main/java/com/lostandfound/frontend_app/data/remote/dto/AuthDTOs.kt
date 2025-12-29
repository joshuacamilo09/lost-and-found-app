package com.lostandfound.frontend_app.data.remote.dto

// Para o endpoint /api/auth/login
data class LoginRequestDTO(
    val username: String,
    val password: String
)

// Para o endpoint /api/auth/register
data class RegisterRequestDTO(
    val username: String,
    val email: String,
    val password: String
)

// Resposta comum que traz o Token
data class AuthResponseDTO(
    val token: String
)