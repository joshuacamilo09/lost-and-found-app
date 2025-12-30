package com.lostandfound.service.chat;

import com.lostandfound.dto.chat.conversaton.ConversationResponse;
import com.lostandfound.dto.chat.message.MessageRequest;
import com.lostandfound.dto.chat.message.MessageResponse;
import com.lostandfound.dto.conversation.ConversationSummaryDTO;
import java.util.List;

public interface ChatService {
    ConversationResponse createConversation(Long itemId, Long otherUserId);
    MessageResponse sendMessage(Long conversationId, MessageRequest request);
    List<ConversationResponse> getMyConversations();
    List<MessageResponse> getMessages(Long conversationId);
    List<ConversationSummaryDTO> getMyConversationsSummary();
}