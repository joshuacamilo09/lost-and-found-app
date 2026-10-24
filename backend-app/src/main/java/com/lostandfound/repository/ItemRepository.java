package com.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lostandfound.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Buscar por status (LOST ou FOUND)
    List<Item> findByStatus(Item.ItemStatus status);

    // Buscar por categoria
    List<Item> findByCategoryId(Long categoryId);

    // Buscar por status e categoria
    List<Item> findByStatusAndCategoryId(Item.ItemStatus status, Long categoryId);

    // Buscar itens de um utilizador específico
    List<Item> findByUserId(Long userId);

    // Buscar itens não resolvidos
    List<Item> findByResolvedFalse();

    // Buscar itens resolvidos
    List<Item> findByResolvedTrue();

    // Buscar por título (pesquisa)
    List<Item> findByTitleContainingIgnoreCase(String title);

    // Buscar por título e status
    List<Item> findByTitleContainingIgnoreCaseAndStatus(String title, Item.ItemStatus status);

    // Buscar itens recentes (ordenados por data de criação)
    List<Item> findAllByOrderByCreatedAtDesc();

    // Buscar itens por status, ordenados por data
    List<Item> findByStatusOrderByCreatedAtDesc(Item.ItemStatus status);

    // Buscar itens dentro de um período
    List<Item> findByItemDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar itens próximos de uma localização (usando SQL nativo)
    @Query(value = "SELECT * FROM items i WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(i.latitude)) * " +
            "cos(radians(i.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(i.latitude)))) < :distance " +
            "AND i.resolved = false " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(i.latitude)) * " +
            "cos(radians(i.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(i.latitude))))",
            nativeQuery = true)
    List<Item> findItemsNearLocation(@Param("latitude") Double latitude,
                                     @Param("longitude") Double longitude,
                                     @Param("distance") Double distance);

    // Contar itens por status
    Long countByStatus(Item.ItemStatus status);

    // Contar itens não resolvidos de um utilizador
    Long countByUserIdAndResolvedFalse(Long userId);
}