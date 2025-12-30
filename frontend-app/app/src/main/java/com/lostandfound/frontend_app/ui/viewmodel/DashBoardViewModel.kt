package com.lostandfound.frontend_app.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.item.ItemResponseDTO
import com.lostandfound.frontend_app.data.repository.ItemRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: ItemRepository) : ViewModel() {

    var items by mutableStateOf<List<ItemResponseDTO>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.getAllItems()
                if (response.isSuccessful) {
                    items = response.body() ?: emptyList()
                } else {
                    errorMessage = "Erro ao carregar itens"
                }
            } catch (e: Exception) {
                errorMessage = "Falha na ligação"
            } finally {
                isLoading = false
            }
        }
    }
}