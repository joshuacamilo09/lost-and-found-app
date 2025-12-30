package com.lostandfound.frontend_app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.user.UserUpdateRequestDTO
import com.lostandfound.frontend_app.data.repository.ItemRepository
import com.lostandfound.frontend_app.util.TokenManager
import kotlinx.coroutines.launch
import com.lostandfound.frontend_app.data.remote.dto.user.UserResponseDTO

class ProfileViewModel(
    private val repository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // No ProfileViewModel.kt
    var userProfile by mutableStateOf<UserResponseDTO?>(null)
        private set
    var isLoading by mutableStateOf(false)
    var isDarkMode by mutableStateOf(false)

    // Estados para edição
    var editFullName by mutableStateOf("")
    var editPhoneNumber by mutableStateOf("")
    var editLocation by mutableStateOf("")

    fun loadProfile() {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getMyProfile("Bearer $token")
                if (response.isSuccessful) {
                    userProfile = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun prepareEdit() {
        userProfile?.let {
            editFullName = it.fullName ?: ""    // Verifique se aqui não está 'it.location'
            editPhoneNumber = it.phoneNumber ?: ""
            editLocation = it.location ?: ""
        }
    }

    fun saveProfileChanges(onSuccess: () -> Unit) {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val request = UserUpdateRequestDTO(
                    fullName = editFullName,
                    phoneNumber = editPhoneNumber,
                    location = editLocation,
                    profileImageUrl = userProfile?.profileImageUrl
                )
                val response = repository.updateMyProfile("Bearer $token", request)
                if (response.isSuccessful) {
                    userProfile = response.body()
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        isDarkMode = enabled
    }
}