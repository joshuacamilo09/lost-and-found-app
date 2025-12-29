package com.lostandfound.service.chat;

import com.lostandfound.dto.chat.conversaton.ConversationResponse;
import com.lostandfound.dto.chat.message.MessageRequest;
import com.lostandfound.dto.chat.message.MessageResponse;
import com.lostandfound.model.*;
import com.lostandfound.repository.ConversationRepository;
import com.lostandfound.repository.ItemRepository;
import com.lostandfound.repository.MessageRepository;
import com.lostandfound.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ChatServiceImpl(ConversationRepository conversationRepository,
                           MessageRepository messageRepository,
                           UserRepository userRepository,
                           ItemRepository itemRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ConversationResponse createConversation(Long itemId, Long otherUserId) {
        User me = getAuthenticatedUser();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Outro utilizador não encontrado"));

        // Só permite conversas LOST ↔ FOUND
        if (item.getStatus() == (item.getUser().getId().equals(otherUser.getId()) ? Item.ItemStatus.LOST : Item.ItemStatus.FOUND)) {
            throw new RuntimeException("Conversas só podem ser entre LOST ↔ FOUND");
        }

        // Verifica se já existe conversa
        Conversation conversation = conversationRepository
                .findByItemAndParticipants(itemId, me.getId(), otherUser.getId())
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setItem(item);
                    c.getParticipants().add(me);
                    c.getParticipants().add(otherUser);
                    return conversationRepository.save(c);
                });

        return mapToConversationResponse(conversation);
    }

   @Override
    public MessageResponse sendMessage(Long conversationId, MessageRequest request) {
    User me = getAuthenticatedUser();
    Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

    if (!conversation.getParticipants().stream().anyMatch(p -> p.getId().equals(me.getId()))) {
        throw new RuntimeException("Não tens permissão para enviar mensagem nesta conversa");
    }

    // Bloqueia conversa se item resolvido ou conversa desativada
    if (!conversation.getActive() || conversation.getItem().getResolved()) {
        throw new RuntimeException("Conversa fechada: não é possível enviar mensagens");
    }

    Message message = new Message();
    message.setConversation(conversation);
    message.setSender(me);
    message.setContent(request.getContent());
    message.setSentAt(LocalDateTime.now());

    conversation.setLastMessageAt(LocalDateTime.now()); // atualiza timestamp
    conversationRepository.save(conversation);

    return mapToMessageResponse(messageRepository.save(message));
}


    @Override
    public List<ConversationResponse> getMyConversations() {
        User me = getAuthenticatedUser();
        return conversationRepository.findByParticipantId(me.getId())
                .stream()
                .map(this::mapToConversationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getMessages(Long conversationId) {
        User me = getAuthenticatedUser();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (!conversation.getParticipants().stream().anyMatch(p -> p.getId().equals(me.getId()))) {
            throw new RuntimeException("Não tens permissão para ver mensagens nesta conversa");
        }

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    // ========================= HELPERS =========================
    private User getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Utilizador autenticado não encontrado"));
}

    private ConversationResponse mapToConversationResponse(Conversation conversation) {
        ConversationResponse dto = new ConversationResponse();
        dto.setId(conversation.getId());
        dto.setItemId(conversation.getItem().getId());
        List<User> participants = conversation.getParticipants().stream().toList();
        if (participants.size() >= 1) {
            dto.setUser1Id(participants.get(0).getId());
            dto.setUser1Username(participants.get(0).getUsername());
        }
        if (participants.size() >= 2) {
            dto.setUser2Id(participants.get(1).getId());
            dto.setUser2Username(participants.get(1).getUsername());
        }
        return dto;
    }

    private MessageResponse mapToMessageResponse(Message message) {
        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getSentAt());
        return dto;
    }   
}