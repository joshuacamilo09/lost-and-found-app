package com.lostandfound.frontend_app.data.remote.dto.chat

data class ChatResponseDTO(
    val id: Long,
    val itemId: Long,
    val user1Id: Long,
    val user2Id: Long
    // o backend agora envia o itemId também
)