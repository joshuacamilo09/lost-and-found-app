package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lostandfound.model.Message;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId,
                                                          @Param("userId") Long userId);

    // Alterado para Optional para maior segurança no Service
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId " +
            "ORDER BY m.sentAt DESC LIMIT 1")
    Optional<Message> findLastMessageByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId " +
            "AND m.sender.id != :userId AND m.isRead = false")
    Long countUnreadMessagesByConversationAndUser(@Param("conversationId") Long conversationId, 
                                                  @Param("userId") Long userId);
}