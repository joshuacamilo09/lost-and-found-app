package com.lostandfound.frontend_app.data.remote.dto.message

data class MessageResponseDTO(
    val id: Long,
    val content: String,
    val senderId: Long,
    val senderUsername: String,
    val createdAt: Any
)