package com.lostandfound.frontend_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lostandfound.frontend_app.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    recipientId: Long,
    viewModel: ChatViewModel,
    itemId: Long,
    token: String,
    onBack: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Inicializa o chat quando a tela abre
    LaunchedEffect(itemId, recipientId) {
        viewModel.initChat(itemId, recipientId, token)
    }

    // Auto-scroll para a última mensagem
    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar Dinâmico
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD1E4FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.recipientName.take(1).uppercase(),
                                color = Color(0xFF004881),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = viewModel.recipientName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = viewModel.itemTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.messages) { msg ->
                    // Verifica se a mensagem é sua (ajuste a lógica de ID conforme seu backend)
                    val isMine = msg.senderId != recipientId

                    MessageBubble(
                        content = msg.content,
                        time = "Enviado", // Idealmente formatar msg.timestamp aqui
                        isMine = isMine
                    )
                }
            }

            // Input Area
            Surface(tonalElevation = 2.dp, color = Color.White) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.newMessageText,
                        onValueChange = { viewModel.newMessageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Mensagem...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF2F2F7),
                            focusedContainerColor = Color(0xFFF2F2F7),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.onSendClick() },
                        enabled = viewModel.newMessageText.isNotBlank(),
                        modifier = Modifier.background(
                            if (viewModel.newMessageText.isNotBlank()) Color(0xFF2F61FF) else Color.LightGray,
                            CircleShape
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(content: String, time: String, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMine) Color(0xFF2F61FF) else Color(0xFFF2F2F7),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 2.dp,
                bottomEnd = if (isMine) 2.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = content,
                    color = if (isMine) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = time,
                    color = if (isMine) Color.White.copy(0.7f) else Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 10.sp
                )
            }
        }
    }
}