package com.lostandfound.controller;

import com.lostandfound.dto.item.ItemRequestDTO;
import com.lostandfound.dto.item.ItemResponseDTO;
import com.lostandfound.model.Item.ItemStatus;
import com.lostandfound.service.item.ItemService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // ========================= CREATE =========================
   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ItemResponseDTO> createItem(
        @RequestPart("item") ItemRequestDTO dto, 
        @RequestPart(value = "image", required = false) MultipartFile image) {
    
    // Agora passamos o DTO e o arquivo para o Service
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(itemService.createItem(dto, image));
}

    // ========================= READ =========================
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByStatus(
            @PathVariable ItemStatus status) {
        return ResponseEntity.ok(itemService.getItemsByStatus(status));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByCategory(
            @PathVariable String categoryNameString) {
        return ResponseEntity.ok(itemService.getItemsByCategory(categoryNameString));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ItemResponseDTO>> getMyItems() {
        return ResponseEntity.ok(itemService.getMyItems());
    }


    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ItemResponseDTO> resolveItem(
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.markAsResolved(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
