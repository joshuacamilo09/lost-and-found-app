package com.lostandfound.frontend_app.data.remote.API


import com.lostandfound.frontend_app.data.remote.dto.AuthResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.CategoryResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.ChatResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.ItemRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.ItemResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.LoginRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.MessageRequestDTO
import com.lostandfound.frontend_app.data.remote.dto.MessageResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.RegisterRequestDTO
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

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryResponseDTO>>

    // No ApiService.kt
    @POST("api/chats/user/{recipientId}")
    suspend fun createOrGetChat(@Path("recipientId") recipientId: Long): Response<ChatResponseDTO>

    @GET("api/chat/conversations/{chatId}/messages")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long
    ): Response<List<MessageResponseDTO>>

    @POST("api/chat/conversations/{chatId}/messages") // Adicionado /chat/conversations/
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long,
        @Body messageRequest: MessageRequestDTO
    ): Response<MessageResponseDTO>

    // No Android: ApiService.kt
    // Em ApiService.kt
    // No ApiService.kt
    @POST("api/chat/conversations")
    suspend fun createConversation(
        @Header("Authorization") token: String, // Adicione o token aqui também!
        @Query("itemId") itemId: Long,          // Query mapeia para @RequestParam
        @Query("otherUserId") otherUserId: Long
    ): Response<ChatResponseDTO>// Use ChatResponseDTO se for o mesmo que ConversationResponse

    @Multipart
    @POST("api/items")
    suspend fun createItem(
        @Part("item") item: ItemRequestDTO, // O JSON do DTO
        @Part image: MultipartBody.Part? // A Imagem
    ): Response<ItemResponseDTO>

    // No teu ficheiro ApiService.kt, dentro da interface
    @GET("api/items/{id}")
    suspend fun getItemById(@Path("id") id: Long): Response<ItemResponseDTO>
}