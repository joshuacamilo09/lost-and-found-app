package com.lostandfound.frontend_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostandfound.frontend_app.data.remote.dto.message.MessageResponseDTO
import com.lostandfound.frontend_app.data.remote.dto.conversation.ConversationSummaryDTO // Import novo
import com.lostandfound.frontend_app.data.repository.ItemRepository
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ItemRepository) : ViewModel() {

    // --- Estado para a Lista de Conversas (Inbox) ---
    var conversations by mutableStateOf<List<ConversationSummaryDTO>>(emptyList())
    var isLoadingConversations by mutableStateOf(false)

    // --- Estado para a Conversa Individual ---
    var messages by mutableStateOf<List<MessageResponseDTO>>(emptyList())
    var chatId by mutableStateOf<Long?>(null)
    var newMessageText by mutableStateOf("")
    var recipientName by mutableStateOf("Carregando...")
    var itemTitle by mutableStateOf("Item...")

    private var userToken: String = ""

    // 1. CARREGAR TODAS AS CONVERSAS (Para a tua tela de Inbox)
    fun loadMyConversations(token: String) {
        this.userToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        isLoadingConversations = true

        viewModelScope.launch {
            try {
                val response = repository.getMyConversations(userToken)
                if (response.isSuccessful) {
                    conversations = response.body() ?: emptyList()
                    Log.d("CHAT_DEBUG", "Conversas carregadas: ${conversations.size}")
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Erro ao carregar lista de conversas: ${e.message}")
            } finally {
                isLoadingConversations = false
            }
        }
    }

    // 2. INICIAR UM CHAT ESPECÍFICO (Melhorado com os nomes dos métodos do Repo)
    fun initChat(itemId: Long, recipientId: Long, token: String) {
        this.userToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        viewModelScope.launch {
            try {
                if (itemId == 0L || recipientId == 0L) return@launch

                // Buscamos info do Item para a TopBar
                val itemResponse = repository.getItemById(itemId)
                if (itemResponse.isSuccessful) {
                    val itemData = itemResponse.body()
                    itemTitle = itemData?.title ?: "Item"
                    recipientName = itemData?.username ?: "Usuário"
                }

                // Criar ou recuperar conversa
                val response = repository.createConversation(userToken, itemId, recipientId)

                if (response.isSuccessful) {
                    chatId = response.body()?.id
                    loadMessages()
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Falha no initChat: ${e.message}")
            }
        }
    }

    // 3. CARREGAR MENSAGENS
    fun loadMessages() {
        val currentId = chatId ?: return
        viewModelScope.launch {
            try {
                val response = repository.getChatMessages(userToken, currentId)
                if (response.isSuccessful) {
                    messages = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Erro ao carregar mensagens: ${e.message}")
            }
        }
    }

    // 4. ENVIAR MENSAGEM
    fun onSendClick() {
        val currentChatId = chatId ?: return
        if (newMessageText.isBlank()) return

        viewModelScope.launch {
            try {
                val response = repository.sendMessage(userToken, currentChatId, newMessageText)
                if (response.isSuccessful) {
                    newMessageText = ""
                    loadMessages() // Recarrega para mostrar a nova mensagem
                }
            } catch (e: Exception) {
                Log.e("CHAT_DEBUG", "Falha ao enviar: ${e.message}")
            }
        }
    }
}