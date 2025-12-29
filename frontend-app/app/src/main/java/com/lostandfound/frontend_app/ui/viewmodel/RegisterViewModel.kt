package com.lostandfound.frontend_app.ui.viewmodel


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.RegisterRequestDTO
import com.lostandfound.frontend_app.data.repository.AuthRepository
import com.lostandfound.frontend_app.util.TokenManager
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Estados da tela
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun onRegisterClick() {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Preencha todos os campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequestDTO(username, email, password)
                val response = repository.register(request)

                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        tokenManager.saveToken(token)
                        isSuccess = true
                    }
                } else {
                    errorMessage = "Erro no registo: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Falha na ligação ao servidor"
            } finally {
                isLoading = false
            }
        }
    }
}