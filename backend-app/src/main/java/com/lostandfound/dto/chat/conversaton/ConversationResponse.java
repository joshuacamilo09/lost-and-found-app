package com.lostandfound.dto.chat.conversaton;

import java.util.List;

import com.lostandfound.dto.chat.message.MessageResponse;

public class ConversationResponse {

    private Long id;
    private Long itemId;
    private String itemTitle;
    private Long user1Id;
    private String user1Username;
    private Long user2Id;
    private String user2Username;
    private List<MessageResponse> messages;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }

    public String getUser1Username() { return user1Username; }
    public void setUser1Username(String user1Username) { this.user1Username = user1Username; }

    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }

    public String getUser2Username() { return user2Username; }
    public void setUser2Username(String user2Username) { this.user2Username = user2Username; }

    public List<MessageResponse> getMessages() { return messages; }
    public void setMessages(List<MessageResponse> messages) { this.messages = messages; }
}
