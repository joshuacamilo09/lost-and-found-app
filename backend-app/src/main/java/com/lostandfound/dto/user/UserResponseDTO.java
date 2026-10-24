package com.lostandfound.dto.user;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String location; // Corrigido para minúscula
    private String profileImageUrl;
    private long itemsPublished;
    private long itemsResolved;

    // Construtor exatamente na ordem que o Controller usa
    public UserResponseDTO(Long id, String username, String email, String fullName, 
                           String phoneNumber, String location, String profileImageUrl, 
                           long itemsPublished, long itemsResolved) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.profileImageUrl = profileImageUrl;
        this.itemsPublished = itemsPublished;
        this.itemsResolved = itemsResolved;
    }

    // Getters (Importantes para o Spring gerar o JSON)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLocation() { return location; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getItemsPublished() { return itemsPublished; }
    public long getItemsResolved() { return itemsResolved; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLocation(String location) { this.location = location; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setItemsPublished(long itemsPublished) { this.itemsPublished = itemsPublished; }
    public void setItemsResolved(long itemsResolved) { this.itemsResolved = itemsResolved; }
}