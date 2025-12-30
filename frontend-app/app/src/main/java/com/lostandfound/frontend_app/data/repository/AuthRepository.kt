package com.lostandfound.frontend_app.data.repository

import com.lostandfound.frontend_app.data.remote.API.ApiService
import com.lostandfound.frontend_app.data.remote.dto.auth.AuthResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.auth.LoginRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.auth.RegisterRequestDTO
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequestDTO): Response<AuthResponseDTO> {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequestDTO): Response<AuthResponseDTO> {
        return apiService.register(registerRequest)
    }
}