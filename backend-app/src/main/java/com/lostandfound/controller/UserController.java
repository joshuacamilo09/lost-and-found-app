package com.lostandfound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lostandfound.dto.user.UserResponseDTO;
import com.lostandfound.dto.user.UserUpdateRequestDTO;
import com.lostandfound.model.User;
import com.lostandfound.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Obter perfil do utilizador autenticado
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(toDTO(user));
    }

    // 🔹 Atualizar perfil
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @RequestBody UserUpdateRequestDTO request
    ) {
        User updatedUser = userService.updateCurrentUser(toEntity(request));
        return ResponseEntity.ok(toDTO(updatedUser));
    }

    // 🔹 Desativar conta
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateAccount() {
        userService.deactivateCurrentUser();
        return ResponseEntity.noContent().build();
    }

    // ====== Mappers ======
   private UserResponseDTO toDTO(User user) {
    long published = user.getItems().size();
    
    long resolved = user.getItems().stream()
            .filter(item -> item.getStatus() != null && "RESOLVED".equals(item.getStatus().toString())) 
            .count();

    return new UserResponseDTO(
            user.getId(),           // 1
            user.getUsername(),     // 2
            user.getEmail(),        // 3
            user.getFullName(),     // 4
            user.getPhoneNumber(),  // 5
            user.getLocation(),     // 6
            user.getProfileImageUrl(), // 7 (Mova para aqui se o Android esperar nesta ordem)
            published,              // 8
            resolved                // 9
            
    );
}

    private User toEntity(UserUpdateRequestDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setLocation(dto.getLocation());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        return user;
    }
}