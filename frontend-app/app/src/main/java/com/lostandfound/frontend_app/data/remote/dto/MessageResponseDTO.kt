package com.lostandfound.frontend_app.data.remote.dto

data class MessageResponseDTO(
    val id: Long,
    val content: String,
    val senderId: Long,
    val senderUsername: String,
    val chatId: Long,
    val timestamp: String // Ou LocalDateTime se você tiver um conversor
)