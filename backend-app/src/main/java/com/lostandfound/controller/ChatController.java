package com.lostandfound.controller;

import com.lostandfound.dto.chat.conversaton.ConversationResponse;
import com.lostandfound.dto.chat.message.MessageRequest;
import com.lostandfound.dto.chat.message.MessageResponse;
import com.lostandfound.service.chat.ChatService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // ========================= CREATE CONVERSATION =========================
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestParam Long itemId,
            @RequestParam Long otherUserId) {
        ConversationResponse conversation = chatService.createConversation(itemId, otherUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    // ========================= SEND MESSAGE =========================
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody MessageRequest request) {
        MessageResponse message = chatService.sendMessage(conversationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // ========================= LIST CONVERSATIONS =========================
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getMyConversations() {
        List<ConversationResponse> conversations = chatService.getMyConversations();
        return ResponseEntity.ok(conversations);
    }

    // ========================= LIST MESSAGES =========================
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long conversationId) {
        List<MessageResponse> messages = chatService.getMessages(conversationId);
        return ResponseEntity.ok(messages);
    }
}
