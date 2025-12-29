package com.lostandfound.dto.user;

import com.lostandfound.model.User;

public class UserUpdateRequestDTO {

    private String fullName;
    private String phoneNumber;
    private String profileImageUrl;

    public UserUpdateRequestDTO() {}

    public UserUpdateRequestDTO(String fullName, String phoneNumber, String profileImageUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    // --- Getters ---
    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // --- Setters ---
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateUserData(User user, UserUpdateRequestDTO dto) {
    if (dto.getFullName() != null) user.setFullName(dto.getFullName());
    if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
    if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());
}
}