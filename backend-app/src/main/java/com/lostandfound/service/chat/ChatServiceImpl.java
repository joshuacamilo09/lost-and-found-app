package com.lostandfound.service.chat;

import com.lostandfound.dto.chat.conversaton.ConversationResponse;
import com.lostandfound.dto.chat.message.MessageRequest;
import com.lostandfound.dto.chat.message.MessageResponse;
import com.lostandfound.dto.conversation.ConversationSummaryDTO;
import com.lostandfound.model.*;
import com.lostandfound.repository.ConversationRepository;
import com.lostandfound.repository.ItemRepository;
import com.lostandfound.repository.MessageRepository;
import com.lostandfound.repository.UserRepository;
import com.lostandfound.service.user.UserService;

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
    private final UserService userService;

    public ChatServiceImpl(ConversationRepository conversationRepository,
                           MessageRepository messageRepository,
                           UserRepository userRepository,
                           ItemRepository itemRepository,
                           UserService userService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
public List<ConversationSummaryDTO> getMyConversationsSummary() {
    // CORREÇÃO: Usamos o método já existente na classe para pegar o usuário logado
    User currentUser = getAuthenticatedUser();
    Long currentUserId = currentUser.getId();
    
    List<Conversation> conversations = conversationRepository.findByParticipantIdOrderByLastMessageAtDesc(currentUserId);

    return conversations.stream().map(conv -> {
        // 1. Descobrir quem é o OUTRO usuário (o destinatário)
        User recipient = conv.getParticipants().stream()
            .filter(p -> !p.getId().equals(currentUserId))
            .findFirst()
            .orElse(null);

        // 2. Buscar a última mensagem
        Message lastMsg = messageRepository.findLastMessageByConversationId(conv.getId()).orElse(null);

        // 3. Contar mensagens não lidas
        long unread = messageRepository.countUnreadMessagesByConversationAndUser(conv.getId(), currentUserId);

        // 4. Criar o DTO (Certifique-se que os argumentos batem com o construtor do DTO)
        return new ConversationSummaryDTO(
            conv.getId(),
            recipient != null ? recipient.getId() : null,
            recipient != null ? recipient.getUsername() : "Usuário Desconhecido",
            conv.getItem() != null ? conv.getItem().getTitle() : "Item s/ Título",
            lastMsg != null ? lastMsg.getContent() : "Nenhuma mensagem",
            conv.getLastMessageAt() != null ? conv.getLastMessageAt().toString() : "",
            (int) unread,
            conv.getItem() != null ? conv.getItem().getId() : null
        );
    }).collect(Collectors.toList());
}

    @Override
    public ConversationResponse createConversation(Long itemId, Long otherUserId) {
        User me = getAuthenticatedUser();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Outro utilizador não encontrado"));

        if (item.getUser().getId().equals(me.getId()) && item.getUser().getId().equals(otherUser.getId())) {
             throw new RuntimeException("Não podes criar uma conversa contigo próprio");
        }

        Conversation conversation = conversationRepository
                .findByItemAndParticipants(itemId, me.getId(), otherUser.getId())
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setItem(item);
                    c.getParticipants().add(me);
                    c.getParticipants().add(otherUser);
                    c.setLastMessageAt(LocalDateTime.now());
                    return conversationRepository.save(c);
                });

        return mapToConversationResponse(conversation);
    }

    @Override
    public MessageResponse sendMessage(Long conversationId, MessageRequest request) {
        User me = getAuthenticatedUser();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (conversation.getParticipants().stream().noneMatch(p -> p.getId().equals(me.getId()))) {
            throw new RuntimeException("Não tens permissão para enviar mensagem nesta conversa");
        }

        if (!conversation.getActive() || (conversation.getItem() != null && conversation.getItem().getResolved())) {
            throw new RuntimeException("Conversa fechada ou item resolvido");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(me);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());

        conversation.setLastMessageAt(LocalDateTime.now());
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

        if (conversation.getParticipants().stream().noneMatch(p -> p.getId().equals(me.getId()))) {
            throw new RuntimeException("Sem permissão");
        }

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
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