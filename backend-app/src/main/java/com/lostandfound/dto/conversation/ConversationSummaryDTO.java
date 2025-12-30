package com.lostandfound.dto.conversation;

public class ConversationSummaryDTO {

private Long chatId;
    private Long itemId;
    private Long recipientId;
    private String recipientName;
    private String itemTitle;
    private String lastMessage;
    private String timestamp;
    private int unreadCount;

    // Mantenha APENAS este construtor
    public ConversationSummaryDTO(Long chatId,
                                  Long recipientId,
                                  String recipientName,
                                  String itemTitle,
                                  String lastMessage,
                                  String timestamp,
                                  int unreadCount,
                                  Long itemId) {
        this.chatId = chatId;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.itemTitle = itemTitle;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.itemId = itemId;
    }

    // Getters e Setters

    public ConversationSummaryDTO(Long id, Long id2, Long id3, String username, String title, String timestamp2,
            Object unreadCount2, int i) {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
