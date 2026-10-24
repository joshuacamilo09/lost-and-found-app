package com.lostandfound.frontend_app.data.remote.dto.conversation

data class ConversationSummaryDTO(
    val chatId: Long,
    val recipientId: Long,
    val itemId: Long,
    val recipientName: String,
    val itemTitle: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int
)