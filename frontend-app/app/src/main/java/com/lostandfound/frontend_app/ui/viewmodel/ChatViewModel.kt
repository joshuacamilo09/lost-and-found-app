package com.lostandfound.frontend_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.ChatResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.MessageResponseDTO
import com.lostandfound.frontend_app.data.repository.ItemRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ChatViewModel(private val repository: ItemRepository) : ViewModel() {

    var messages by mutableStateOf<List<MessageResponseDTO>>(emptyList())
    var chatId by mutableStateOf<Long?>(null)
    var newMessageText by mutableStateOf("")

    // Variáveis que agora serão preenchidas via busca no repositório
    var recipientName by mutableStateOf("Carregando...")
    var itemTitle by mutableStateOf("Item...")

    private var userToken: String = ""

    fun initChat(itemId: Long, recipientId: Long, token: String) {
        this.userToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        viewModelScope.launch {
            try {
                if (itemId == 0L || recipientId == 0L) return@launch

                // 1. Buscamos as informações do Item para preencher a TopBar
                // Assume-se que seu repositório tem uma função getItemById que retorna Response<ItemResponseDTO>
                val itemResponse = repository.getItemById(itemId)
                if (itemResponse.isSuccessful) {
                    val itemData = itemResponse.body()
                    itemTitle = itemData?.title ?: "Item"
                    recipientName = itemData?.username ?: "Usuário"
                }

                // 2. Iniciamos/Criamos a conversa
                val response: Response<ChatResponseDTO> = repository.createConversation(
                    token = userToken,
                    itemId = itemId,
                    otherUserId = recipientId
                )

                if (response.isSuccessful) {
                    chatId = response.body()?.id
                    Log.d("CHAT_DEBUG", "Chat iniciado: $chatId")
                    loadMessages()
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Falha no initChat: ${e.message}")
            }
        }
    }

    fun loadMessages() {
        val currentId = chatId ?: return
        viewModelScope.launch {
            try {
                val response: Response<List<MessageResponseDTO>> = repository.getChatMessages(userToken, currentId)
                if (response.isSuccessful) {
                    messages = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Erro ao carregar mensagens: ${e.message}")
            }
        }
    }

    fun onSendClick() {
        val currentChatId = chatId ?: return
        if (newMessageText.isBlank()) return

        viewModelScope.launch {
            try {
                val response = repository.sendMessage(userToken, currentChatId, newMessageText)
                if (response.isSuccessful) {
                    newMessageText = ""
                    loadMessages()
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Falha de Rede: ${e.message}")
            }
        }
    }
}