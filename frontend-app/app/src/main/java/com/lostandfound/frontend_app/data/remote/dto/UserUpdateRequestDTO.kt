package com.lostandfound.frontend_app.data.remote.dto.user

data class UserUpdateRequestDTO(
    val fullName: String?,
    val phoneNumber: String?,
    val location: String?,
    val profileImageUrl: String?
)