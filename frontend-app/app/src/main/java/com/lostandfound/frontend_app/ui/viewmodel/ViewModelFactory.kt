package com.lostandfound.frontend_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lostandfound.frontend_app.data.repository.ItemRepository
import com.lostandfound.frontend_app.util.TokenManager

class ViewModelFactory(
    private val repository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChatViewModel::class.java) ->
                ChatViewModel(repository) as T

            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T

            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) ->
                ItemDetailViewModel(repository, tokenManager) as T

            // CORREÇÃO AQUI: Faltava passar o tokenManager
            modelClass.isAssignableFrom(CreateItemViewModel::class.java) ->
                CreateItemViewModel(repository, tokenManager) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository, tokenManager) as T

            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        }
    }
}