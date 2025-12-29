package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lostandfound.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Buscar mensagens de uma conversa específica
    List<Message> findByConversationId(Long conversationId);

    // Buscar mensagens de uma conversa ordenadas por data (mais antigas primeiro)
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);

    // Buscar mensagens de uma conversa ordenadas por data (mais recentes primeiro)
    List<Message> findByConversationIdOrderBySentAtDesc(Long conversationId);

    // Buscar mensagens enviadas por um utilizador
    List<Message> findBySenderId(Long senderId);

    // Buscar mensagens não lidas de uma conversa
    List<Message> findByConversationIdAndIsReadFalse(Long conversationId);

    // Buscar mensagens não lidas de um utilizador numa conversa
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId,
                                                          @Param("userId") Long userId);

    // Contar mensagens não lidas numa conversa
    Long countByConversationIdAndIsReadFalse(Long conversationId);

    // Contar mensagens não lidas de um utilizador em todas as suas conversas
    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c JOIN c.participants p " +
            "WHERE p.id = :userId AND m.sender.id != :userId AND m.isRead = false")
    Long countUnreadMessagesByUser(@Param("userId") Long userId);

    // Buscar última mensagem de uma conversa
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
            "ORDER BY m.sentAt DESC LIMIT 1")
    Message findLastMessageByConversationId(@Param("conversationId") Long conversationId);

    // Contar total de mensagens numa conversa
    Long countByConversationId(Long conversationId);
}