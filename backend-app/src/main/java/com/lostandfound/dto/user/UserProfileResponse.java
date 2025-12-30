package com.lostandfound.dto.user;

import java.time.LocalDateTime;

public record UserProfileResponse(
    Long id,
    String username,
    String fullName,
    String email,
    String phoneNumber,
    String location,
    long totalItemsPublished,  
    long totalItemsResolved,  
    LocalDateTime joinedAt
) {}