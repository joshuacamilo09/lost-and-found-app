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
import com.lostandfound.frontend_app.util.TokenManager // Verifique se este import existe
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

// ADICIONADO "private val" antes de tokenManager
class CreateItemViewModel(
    private val repository: ItemRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var editingItemId by mutableStateOf<Long?>(null)

    // Estados do Formulário
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var categoryName by mutableStateOf("")
    var status by mutableStateOf(ItemStatus.LOST)
    var locationName by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var currentImageUrl by mutableStateOf<String?>(null)

    // Estados de UI
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadItemForEdit(itemId: Long) {
        editingItemId = itemId
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getItemById(itemId)
                if (response.isSuccessful && response.body() != null) {
                    val item = response.body()!!
                    title = item.title
                    description = item.description
                    categoryName = item.categoryName
                    status = item.status
                    locationName = item.locationName ?: ""
                    currentImageUrl = item.imageUrl
                }
            } catch (e: Exception) {
                errorMessage = "Erro ao carregar dados: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveItemClick(context: Context) {
        if (title.isBlank() || categoryName.isBlank()) {
            errorMessage = "Título e Categoria são obrigatórios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Agora o compilador encontrará a referência de tokenManager
                val token = tokenManager.getToken() ?: ""

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

                val response = if (editingItemId == null) {
                    repository.createItem(itemRequest, imagePart)
                } else {
                    repository.updateItem(
                        token = token,
                        id = editingItemId!!,
                        dto = itemRequest,
                        image = imagePart
                    )
                }

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
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
}