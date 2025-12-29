package com.lostandfound.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lostandfound.dto.auth.request.LoginRequestDTO;
import com.lostandfound.dto.auth.request.RegisterRequestDTO;
import com.lostandfound.dto.auth.response.AuthResponseDTO;
import com.lostandfound.model.User;
import com.lostandfound.repository.UserRepository;
import com.lostandfound.security.JwtTokenProvider;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    // Login
    public AuthResponseDTO login(LoginRequestDTO dto) {
        // Autenticar utilizador
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        // Gerar token JWT
        String token = tokenProvider.generateToken(authentication);
        
        return new AuthResponseDTO(token);
    }

    // Registar novo utilizador
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        // Verificar se o username já existe
        if (userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username already exists");
        }

        // Verificar se o email já existe
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Criar novo utilizador
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setActive(true);

        // Guardar na base de dados
        userRepository.save(user);

        // Gerar token JWT
        String token = tokenProvider.generateTokenFromUsername(user.getUsername());
        
        return new AuthResponseDTO(token);
    }
}