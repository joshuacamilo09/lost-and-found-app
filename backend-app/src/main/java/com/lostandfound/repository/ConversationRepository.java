package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lostandfound.model.Conversation;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId")
    List<Conversation> findByParticipantId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId AND c.active = true")
    List<Conversation> findActiveConversationsByUserId(@Param("userId") Long userId);

    List<Conversation> findByItemId(Long itemId);

    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
            "WHERE c.item.id = :itemId AND p1.id = :user1Id AND p2.id = :user2Id")
    Optional<Conversation> findByItemAndParticipants(@Param("itemId") Long itemId,
                                                     @Param("user1Id") Long user1Id,
                                                     @Param("user2Id") Long user2Id);

    // Essencial para a lista de chats (Inbox)
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.id = :userId " +
            "ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<Conversation> findByParticipantIdOrderByLastMessageAtDesc(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Conversation c " +
            "JOIN c.participants p1 JOIN c.participants p2 " +
            "WHERE c.item.id = :itemId AND p1.id = :user1Id AND p2.id = :user2Id")
    Boolean existsByItemAndParticipants(@Param("itemId") Long itemId,
                                        @Param("user1Id") Long user1Id,
                                        @Param("user2Id") Long user2Id);
}