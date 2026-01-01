package com.lostandfound.service.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lostandfound.model.User;
import com.lostandfound.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Obter utilizador autenticado
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilizador autenticado não encontrado"));
    }

public User updateCurrentUser(User updates) {
    User currentUser = getCurrentUser();

    if (updates.getFullName() != null) {
        currentUser.setFullName(updates.getFullName());
    }
    if (updates.getPhoneNumber() != null) {
        currentUser.setPhoneNumber(updates.getPhoneNumber());
    }
    if (updates.getLocation() != null) {
        currentUser.setLocation(updates.getLocation());
    }
    if (updates.getProfileImageUrl() != null) {
        currentUser.setProfileImageUrl(updates.getProfileImageUrl());
    }

    return userRepository.save(currentUser);
}

    // Desativar conta do utilizador autenticado
    public void deactivateCurrentUser() {
        User currentUser = getCurrentUser();
        currentUser.setActive(false);
        userRepository.save(currentUser);
    }

    // Buscar utilizador por ID (público)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }

    // Buscar utilizador por username (público)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }
}