package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lostandfound.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Buscar categoria por nome
    Optional<Category> findByName(String name);

    // Buscar categoria por nome (case insensitive)
    Optional<Category> findByNameIgnoreCase(String name);

    // Buscar todas as categorias ativas
    List<Category> findByActiveTrue();

    // Buscar todas as categorias inativas
    List<Category> findByActiveFalse();

    // Verificar se existe uma categoria com determinado nome
    Boolean existsByName(String name);

    // Verificar se existe uma categoria com determinado nome (case insensitive)
    Boolean existsByNameIgnoreCase(String name);

    // Buscar categorias ordenadas por nome
    List<Category> findAllByOrderByNameAsc();
}