package com.lostandfound.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lostandfound.dto.user.UserResponseDTO;
import com.lostandfound.dto.user.UserUpdateRequestDTO;
import com.lostandfound.model.User;
import com.lostandfound.service.user.UserService;

@RestController
@RequestMapping("/api/users")
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

    // ====== Mappers (por agora aqui) ======

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }

    private User toEntity(UserUpdateRequestDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        return user;
    }
}
