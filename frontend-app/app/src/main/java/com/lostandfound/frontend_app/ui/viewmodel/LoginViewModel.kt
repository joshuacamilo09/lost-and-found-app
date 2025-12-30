package com.lostandfound.frontend_app.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.auth.LoginRequestDTO
import com.lostandfound.frontend_app.data.repository.AuthRepository
import com.lostandfound.frontend_app.util.TokenManager
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun onLoginClick() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Introduza o utilizador e a senha"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequestDTO(username, password)
                val response = repository.login(request)

                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        tokenManager.saveToken(token)
                        isSuccess = true
                    }
                } else {
                    errorMessage = "Credenciais inválidas"
                }
            } catch (e: Exception) {
                errorMessage = "Erro de rede: Verifique a ligação"
            } finally {
                isLoading = false
            }
        }
    }
}