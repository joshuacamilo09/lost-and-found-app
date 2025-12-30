package com.lostandfound.frontend_app.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.repository.ItemRepository
import kotlinx.coroutines.launch
import com.lostandfound.frontend_app.data.remote.dto.item.ItemResponseDTO
class ItemDetailViewModel(private val repository: ItemRepository) : ViewModel() {

    var item by mutableStateOf<ItemResponseDTO?>(null)
    var isLoading by mutableStateOf(false)

    fun loadItem(id: Long) {
        viewModelScope.launch {
            isLoading = true
            val response = repository.getItemById(id)
            if (response.isSuccessful) {
                item = response.body()
            }
            isLoading = false
        }
    }
}
