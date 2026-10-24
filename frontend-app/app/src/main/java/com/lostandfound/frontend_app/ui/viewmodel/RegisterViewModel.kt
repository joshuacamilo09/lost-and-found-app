package com.lostandfound.frontend_app.ui.viewmodel


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.auth.RegisterRequestDTO
import com.lostandfound.frontend_app.data.repository.AuthRepository
import com.lostandfound.frontend_app.util.TokenManager
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // NOVOS ESTADOS
    var phoneNumber by mutableStateOf("")
    var location by mutableStateOf("")
    var fullName by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun onRegisterClick() {
        // 1. Validação local (UX)
        if (username.isBlank() || email.isBlank() || password.isBlank() || fullName.isBlank()) {
            errorMessage = "Preencha todos os campos obrigatórios"
            return
        }

        if (password.length < 6) {
            errorMessage = "A palavra-passe deve ter pelo menos 6 caracteres"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Introduza um e-mail válido"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = RegisterRequestDTO(
                    username = username.trim(),
                    email = email.trim(),
                    password = password, // Não usamos trim aqui para permitir espaços se o user quiser
                    fullName = fullName.trim(),
                    phoneNumber = phoneNumber.trim(),
                    location = location.trim()
                )

                val response = repository.register(request)

                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        tokenManager.saveToken(token)
                        isSuccess = true
                    }
                } else {
                    // Aqui capturamos o erro 400/403 que o Spring envia
                    errorMessage = when (response.code()) {
                        403 -> "Erro de permissão ou dados inválidos"
                        409 -> "Este utilizador ou e-mail já existe"
                        else -> "Erro no registo: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Falha na ligação ao servidor. Verifique a internet."
            } finally {
                isLoading = false
            }
        }
    }
}