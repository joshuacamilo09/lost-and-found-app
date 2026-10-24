package com.lostandfound.frontend_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.item.ItemResponseDTO
import com.lostandfound.frontend_app.data.repository.ItemRepository
import com.lostandfound.frontend_app.util.TokenManager
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val repository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var item by mutableStateOf<ItemResponseDTO?>(null)
    var isLoading by mutableStateOf(false)
    var currentUserId by mutableLongStateOf(-1L)

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            // Tenta ler do cache primeiro se o seu TokenManager tiver essa função
            // currentUserId = tokenManager.getUserId()

            val token = tokenManager.getToken() ?: ""
            if (token.isNotEmpty()) {
                val response = repository.getMyProfile("Bearer $token") // Verifique se falta o "Bearer "
                if (response.isSuccessful) {
                    currentUserId = response.body()?.id ?: -1L
                    println("DEBUG: ID carregado com sucesso: $currentUserId")
                }
            }
        }
    }

    fun loadItem(id: Long) {
        viewModelScope.launch {
            isLoading = true
            val response = repository.getItemById(id)
            if (response.isSuccessful) item = response.body()
            isLoading = false
        }
    }

    fun deleteItem(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val token = tokenManager.getToken() ?: ""

            // Garante o formato Bearer Token
            val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

            // Chama o repository (estou seguindo o padrão .onSuccess que você usou)
            repository.deleteItem(authHeader, id).onSuccess {
                Log.d("DELETE", "Item $id removido com sucesso")
                onSuccess() // Fecha o ecrã
            }.onFailure { error ->
                Log.e("DELETE", "Erro ao apagar: ${error.message}")
            }
            isLoading = false
        }
    }

    fun markAsResolved(id: Long) {
        viewModelScope.launch {
            repository.markAsResolved(id).onSuccess { updated ->
                item = updated
            }
        }
    }
}