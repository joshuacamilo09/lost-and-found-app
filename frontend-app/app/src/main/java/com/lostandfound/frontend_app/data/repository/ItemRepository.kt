package com.lostandfound.frontend_app.data.repository

import com.lostandfound.frontend_app.data.remote.API.ApiService
import com.lostandfound.frontend_app.data.remote.dto.*
import com.lostandfound.frontend_app.data.remote.dto.chat.ChatResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.item.ItemRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.item.ItemResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.message.MessageRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.message.MessageResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.conversation.ConversationSummaryDTO // Importante
import com.lostandfound.frontend_app.data.remote.dto.user.UserResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.user.UserUpdateRequestDTO
import retrofit2.Response
import okhttp3.MultipartBody

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
        return apiService.getMyProfile(token)
    }

    suspend fun updateMyProfile(
        token: String,
        request: UserUpdateRequestDTO
    ): Response<UserResponseDTO> {
        return apiService.updateMyProfile(token, request)
    }
}