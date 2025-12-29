package com.lostandfound.service.chat;

import com.lostandfound.dto.chat.conversaton.ConversationResponse;
import com.lostandfound.dto.chat.message.MessageRequest;
import com.lostandfound.dto.chat.message.MessageResponse;
import java.util.List;

public interface ChatService {

    // Criar nova conversa ligada a um item
    ConversationResponse createConversation(Long itemId, Long otherUserId);

    // Enviar mensagem em uma conversa
    MessageResponse sendMessage(Long conversationId, MessageRequest request);

    // Listar todas as conversas do usuário
    List<ConversationResponse> getMyConversations();

    // Listar mensagens de uma conversa
    List<MessageResponse> getMessages(Long conversationId);
}
