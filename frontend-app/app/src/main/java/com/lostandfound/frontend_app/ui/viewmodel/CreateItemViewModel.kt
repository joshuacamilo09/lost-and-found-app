package com.lostandfound.frontend_app.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.item.ItemRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.item.ItemStatus
import com.lostandfound.frontend_app.data.repository.ItemRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class CreateItemViewModel(private val repository: ItemRepository) : ViewModel() {

    // Estados do Formulário
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var categoryName by mutableStateOf("") // Erro de "unresolved" resolvido aqui
    var status by mutableStateOf(ItemStatus.LOST)
    var locationName by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    // Estados de UI
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreateItemClick(context: Context) {
        if (title.isBlank() || categoryName.isBlank()) {
            errorMessage = "Título e Categoria são obrigatórios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val itemRequest = ItemRequestDTO(
                    title = title,
                    description = description,
                    categoryName = categoryName,
                    status = status,
                    latitude = 0.0,
                    longitude = 0.0,
                    locationName = locationName,
                    itemDateTime = LocalDateTime.now().toString()
                )

                val imagePart = selectedImageUri?.let { uri ->
                    prepareImagePart(context, uri)
                }

                val response = repository.createItem(itemRequest, imagePart)

                if (response.isSuccessful) {
                    isSuccess = true
                } else {
                    errorMessage = "Erro no servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Falha na ligação: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun prepareImagePart(context: Context, uri: Uri): MultipartBody.Part {
        val file = File(context.cacheDir, "temp_image.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
}