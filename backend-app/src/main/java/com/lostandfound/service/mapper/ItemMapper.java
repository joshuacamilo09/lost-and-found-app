package com.lostandfound.service.mapper;

import com.lostandfound.dto.item.ItemResponseDTO;
import com.lostandfound.model.Item;

public class ItemMapper {

    public static ItemResponseDTO toDTO(Item item) {
        if (item == null) return null;

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setStatus(item.getStatus());
        dto.setImageUrl(item.getImageUrl());
        dto.setLatitude(item.getLatitude());
        dto.setLongitude(item.getLongitude());
        dto.setLocationName(item.getLocationName());
        dto.setItemDateTime(item.getItemDateTime());
        dto.setResolved(item.getResolved());
        dto.setCreatedAt(item.getCreatedAt());

        dto.setUserId(item.getUser().getId());
        dto.setUsername(item.getUser().getUsername());

        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());

        return dto;
    }
}
