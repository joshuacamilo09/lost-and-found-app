package com.lostandfound.service.item;

import com.lostandfound.dto.item.ItemRequestDTO;
import com.lostandfound.dto.item.ItemResponseDTO;
import com.lostandfound.model.Item.ItemStatus;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ItemService {

    ItemResponseDTO getItemById(Long id);

    List<ItemResponseDTO> getAllItems();

    List<ItemResponseDTO> getItemsByStatus(ItemStatus status);

    List<ItemResponseDTO> getItemsByCategory(String categoryNameString);

    List<ItemResponseDTO> getMyItems();

    ItemResponseDTO markAsResolved(Long itemId);

    void deleteItem(Long itemId);

    ItemResponseDTO createItem(ItemRequestDTO dto, MultipartFile image);
}
