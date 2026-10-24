package com.lostandfound.frontend_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lostandfound.frontend_app.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesListScreen(
    viewModel: ChatViewModel,
    token: String,
    onBackClick: () -> Unit, // 1. Adicionado parâmetro para voltar
    onChatClick: (chatId: Long, recipientId: Long, itemId: Long) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadMyConversations(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensagens", fontWeight = FontWeight.Bold) },
                // 2. Adicionado o ícone de voltar
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (viewModel.isLoadingConversations) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma conversa ainda", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                items(viewModel.conversations) { conversation ->
                    ConversationItem(
                        name = conversation.recipientName ?: "Usuário",
                        itemTitle = conversation.itemTitle ?: "Item",
                        lastMessage = conversation.lastMessage ?: "Nenhuma mensagem",
                        time = conversation.timestamp?.take(10) ?: "",
                        unreadCount = conversation.unreadCount,
                        onClick = {
                            val chatId = conversation.chatId
                            val recipientId = conversation.recipientId
                            if (chatId != null && recipientId != null) {
                                onChatClick(chatId, recipientId, conversation.itemId ?: 0L)
                            }
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    name: String,
    itemTitle: String,
    lastMessage: String,
    time: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F7)),
            contentAlignment = Alignment.Center
        ) {
            val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
            Text(initial, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = time, fontSize = 12.sp, color = Color.Gray)
            }

            Text(
                text = itemTitle,
                fontSize = 13.sp,
                color = Color(0xFF2F61FF),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = lastMessage,
                fontSize = 14.sp,
                color = if (unreadCount > 0) Color.Black else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            )
        }

        if (unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = unreadCount.toString(), color = Color.White, fontSize = 10.sp)
            }
        }
    }
}