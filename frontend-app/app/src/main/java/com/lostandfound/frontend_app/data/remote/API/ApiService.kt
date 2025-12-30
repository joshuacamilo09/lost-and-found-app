package com.lostandfound.frontend_app.data.remote.API

import com.lostandfound.frontend_app.data.remote.dto.*
import com.lostandfound.frontend_app.data.remote.dto.auth.*
import com.lostandfound.frontend_app.data.remote.dto.chat.ChatResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.item.*
import com.lostandfound.frontend_app.data.remote.dto.message.*
import com.lostandfound.frontend_app.data.remote.dto.conversation.ConversationSummaryDTO // Importe o novo DTO
import com.lostandfound.frontend_app.data.remote.dto.user.UserResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.user.UserUpdateRequestDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<AuthResponseDTO>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDTO): Response<AuthResponseDTO>

    @GET("api/items")
    suspend fun getAllItems(): Response<List<ItemResponseDTO>>

    @GET("api/items/{id}")
    suspend fun getItemById(@Path("id") id: Long): Response<ItemResponseDTO>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryResponseDTO>>

    @Multipart
    @POST("api/items")
    suspend fun createItem(
        @Part("item") item: ItemRequestDTO,
        @Part image: MultipartBody.Part?
    ): Response<ItemResponseDTO>

    // ========================= CHAT (CONVERSATIONS) =========================

    // LISTA DE CHATS (A que vai para o seu novo Dashboard de Conversas)
    @GET("api/chat/conversations")
    suspend fun getMyConversations(
        @Header("Authorization") token: String
    ): Response<List<ConversationSummaryDTO>>

    // CRIAR CONVERSA
    @POST("api/chat/conversations")
    suspend fun createConversation(
        @Header("Authorization") token: String,
        @Query("itemId") itemId: Long,
        @Query("otherUserId") otherUserId: Long
    ): Response<ChatResponseDTO>

    // BUSCAR MENSAGENS DE UM CHAT
    @GET("api/chat/conversations/{conversationId}/messages")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("conversationId") conversationId: Long
    ): Response<List<MessageResponseDTO>>

    // ENVIAR MENSAGEM
    @POST("api/chat/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("conversationId") conversationId: Long,
        @Body messageRequest: MessageRequestDTO
    ): Response<MessageResponseDTO>

    // No teu ApiService.kt
    @GET("api/users/me")
    suspend fun getMyProfile(
        @Header("Authorization") token: String
    ): Response<UserResponseDTO>

    // No seu ItemRepository.kt ou ApiService.kt
    @PUT("api/users/me")
    suspend fun updateMyProfile(
        @Header("Authorization") token: String,
        @Body request: UserUpdateRequestDTO
    ): Response<UserResponseDTO>
}