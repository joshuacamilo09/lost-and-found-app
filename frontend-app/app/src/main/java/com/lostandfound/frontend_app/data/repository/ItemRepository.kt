package com.lostandfound.frontend_app.data.repository

import com.lostandfound.frontend_app.data.remote.API.ApiService
import com.lostandfound.frontend_app.data.remote.dto.*
import retrofit2.Response
import okhttp3.MultipartBody

class ItemRepository(private val apiService: ApiService) {

    suspend fun getAllItems(): Response<List<ItemResponseDTO>> = apiService.getAllItems()

    suspend fun getItemById(id: Long): Response<ItemResponseDTO> = apiService.getItemById(id)

    suspend fun createItem(item: ItemRequestDTO, image: MultipartBody.Part?): Response<ItemResponseDTO> =
        apiService.createItem(item, image)

    suspend fun createConversation(token: String, itemId: Long, otherUserId: Long): Response<ChatResponseDTO> {
        // Garanta que o Bearer está presente antes de enviar para a API
        val tokenFormatado = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return apiService.createConversation(tokenFormatado, itemId, otherUserId)
    }

    suspend fun getChatMessages(token: String, chatId: Long): Response<List<MessageResponseDTO>> {
        return apiService.getChatMessages(token, chatId)
    }

    // No seu ItemRepository.kt
    suspend fun sendMessage(token: String, chatId: Long, content: String): Response<MessageResponseDTO> {
        val request = MessageRequestDTO(content = content)
        return apiService.sendMessage(token, chatId, request)
    }
}