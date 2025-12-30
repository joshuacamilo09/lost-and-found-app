package com.lostandfound.frontend_app.data.remote.dto.item

enum class ItemStatus { LOST, FOUND }

data class ItemRequestDTO(
    val title: String,
    val description: String,
    val categoryName: String, // Certifica-te que é String e tem este nome exato
    val status: ItemStatus,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val itemDateTime: Any
)

data class ItemResponseDTO(
    val id: Long,
    val title: String,
    val description: String,
    val status: ItemStatus,
    val imageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val itemDateTime: Any,
    val resolved: Boolean,
    val userId: Long,
    val username: String,
    val categoryId: Long,
    val categoryName: String
)