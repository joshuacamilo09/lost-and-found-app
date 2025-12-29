package com.lostandfound.frontend_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lostandfound.frontend_app.data.repository.ItemRepository

class ViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(repository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> ItemDetailViewModel(repository) as T
            modelClass.isAssignableFrom(CreateItemViewModel::class.java) -> CreateItemViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel desconhecido")
        }
    }
}
