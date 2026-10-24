package com.lostandfound.frontend_app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("itemsPublished") val itemsPublished: Long,
    @SerializedName("itemsResolved") val itemsResolved: Long
)