package com.lostandfound.service.item;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lostandfound.dto.item.ItemRequestDTO;
import com.lostandfound.dto.item.ItemResponseDTO;
import com.lostandfound.model.Category;
import com.lostandfound.model.Conversation;
import com.lostandfound.model.Item;
import com.lostandfound.model.User;
import com.lostandfound.repository.CategoryRepository;
import com.lostandfound.repository.ConversationRepository;
import com.lostandfound.repository.ItemRepository;
import com.lostandfound.repository.UserRepository;
import com.lostandfound.model.Item.ItemStatus;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ConversationRepository conversationRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ConversationRepository conversationRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.conversationRepository = conversationRepository;
    }


   @Override
    public ItemResponseDTO createItem(ItemRequestDTO dto, MultipartFile image) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setName(dto.getCategoryName());
                    return categoryRepository.save(newCat);
                });

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setStatus(dto.getStatus());
        item.setLatitude(dto.getLatitude());
        item.setLongitude(dto.getLongitude());
        item.setLocationName(dto.getLocationName());
        item.setItemDateTime(dto.getItemDateTime());
        item.setResolved(false);

        // Lógica do Cloudinary
        if (image != null && !image.isEmpty()) {
            try {
                Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dgvgm30cm",
                    "api_key", "398823687392838",
                    "api_secret", "xBWDA4gxTggtjV8S3iNzXbrol1g"
                ));
                
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                item.setImageUrl(uploadResult.get("url").toString());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage());
            }
        } else {
            item.setImageUrl(dto.getImageUrl());
        }

        item.setUser(user);
        item.setCategory(category);
        
        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponseDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        return mapToResponse(item);
    }

    @Override
    public List<ItemResponseDTO> getAllItems() {
        return itemRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDTO> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
public List<ItemResponseDTO> getItemsByCategory(String categoryNameString) {
    return itemRepository.findByTitleContainingIgnoreCase(categoryNameString)
            .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
}

    @Override
    public List<ItemResponseDTO> getMyItems() {
        User user = getAuthenticatedUser();
        return itemRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========================= UPDATE =========================
 @Override
    public ItemResponseDTO markAsResolved(Long itemId) {
    User user = getAuthenticatedUser();
    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item não encontrado"));

    if (!item.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Não tens permissão para alterar este item");
    }

    item.setResolved(true);

    // Fecha conversa relacionada
    Optional<Conversation> conversationOptional = conversationRepository.findByItemId(item.getId()).stream().findFirst();
    conversationOptional.ifPresent(conversation -> {
        conversation.setActive(false);
        conversationRepository.save(conversation);
    });

    return mapToResponse(item);
}


    // ========================= DELETE =========================
    @Override
    public void deleteItem(Long itemId) {
        User user = getAuthenticatedUser();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Não tens permissão para apagar este item");
        }

        itemRepository.delete(item);
    }

    // ========================= HELPERS =========================
    private User getAuthenticatedUser() {
    String username = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Utilizador autenticado não encontrado"));
}

    private ItemResponseDTO mapToResponse(Item item) {
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
