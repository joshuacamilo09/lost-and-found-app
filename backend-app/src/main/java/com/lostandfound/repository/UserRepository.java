package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lostandfound.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar utilizador por username
    Optional<User> findByUsername(String username);

    // Buscar utilizador por email
    Optional<User> findByEmail(String email);

    // Verificar se existe utilizador com determinado username
    Boolean existsByUsername(String username);

    // Verificar se existe utilizador com determinado email
    Boolean existsByEmail(String email);

    // Buscar utilizador por username ou email (útil para login)
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Buscar apenas utilizadores ativos
    Optional<User> findByUsernameAndActiveTrue(String username);

    Optional<User> findByEmailAndActiveTrue(String email);
}