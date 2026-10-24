package com.lostandfound.frontend_app.data.repository

import com.google.gson.Gson
import com.lostandfound.frontend_app.data.remote.API.ApiService
import com.lostandfound.frontend_app.data.remote.dto.chat.ChatResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.item.ItemRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.item.ItemResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.message.MessageRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.message.MessageResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.conversation.ConversationSummaryDTO // Importante
import com.lostandfound.frontend_app.data.remote.dto.user.UserResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.user.UserUpdateRequestDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ItemRepository(private val apiService: ApiService) {

    // ========================= ITEMS =========================

    suspend fun getAllItems(): Response<List<ItemResponseDTO>> = apiService.getAllItems()

    suspend fun getItemById(id: Long): Response<ItemResponseDTO> = apiService.getItemById(id)

    suspend fun createItem(item: ItemRequestDTO, image: MultipartBody.Part?): Response<ItemResponseDTO> =
        apiService.createItem(item, image)

    // ========================= CHAT =========================

    // FUNÇÃO NOVA: Para carregar a lista de conversas (Inbox)
    suspend fun getMyConversations(token: String): Response<List<ConversationSummaryDTO>> {
        val tokenFormatado = formatToken(token)
        return apiService.getMyConversations(tokenFormatado)
    }

    suspend fun createConversation(token: String, itemId: Long, otherUserId: Long): Response<ChatResponseDTO> {
        val tokenFormatado = formatToken(token)
        return apiService.createConversation(tokenFormatado, itemId, otherUserId)
    }

    suspend fun getChatMessages(token: String, conversationId: Long): Response<List<MessageResponseDTO>> {
        val tokenFormatado = formatToken(token)
        return apiService.getChatMessages(tokenFormatado, conversationId)
    }

    suspend fun sendMessage(token: String, conversationId: Long, content: String): Response<MessageResponseDTO> {
        val tokenFormatado = formatToken(token)
        val request = MessageRequestDTO(content = content)
        return apiService.sendMessage(tokenFormatado, conversationId, request)
    }

    private fun formatToken(token: String): String {
        return if (token.startsWith("Bearer ")) token else "Bearer $token"
    }

    suspend fun getMyProfile(token: String): Response<UserResponseDTO> {
        return apiService.getMyProfile(formatToken(token)) // Adicione o formatToken aqui!
    }

    suspend fun updateMyProfile(
        token: String,
        request: UserUpdateRequestDTO
    ): Response<UserResponseDTO> {
        return apiService.updateMyProfile(token, request)
    }

    // No teu ItemRepository
    suspend fun deleteItem(token: String, id: Long): Result<Unit> {
        return try {
            // Se usar Retrofit puro:
            val response = apiService.deleteItem(token, id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erro ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // No seu ItemRepository.kt
    suspend fun updateItem(
        token: String,
        id: Long,
        dto: ItemRequestDTO,
        image: MultipartBody.Part?
    ): Response<ItemResponseDTO> {
        val tokenFormatado = formatToken(token)
        val itemJson = Gson().toJson(dto)
        val itemPart = itemJson.toRequestBody("application/json".toMediaTypeOrNull())

        // Agora enviamos o tokenFormatado como primeiro argumento
        return apiService.updateItem(tokenFormatado, id, itemPart, image)
    }

    suspend fun markAsResolved(id: Long): Result<ItemResponseDTO> {
        return try {
            val response = apiService.markAsResolved(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else Result.failure(Exception("Erro ao atualizar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}