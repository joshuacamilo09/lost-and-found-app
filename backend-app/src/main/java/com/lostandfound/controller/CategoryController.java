package com.lostandfound.controller;

import com.lostandfound.dto.category.CategoryRequestDTO;
import com.lostandfound.dto.category.CategoryResponseDTO;
import com.lostandfound.service.category.CategoryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDTO create(@Valid @RequestBody CategoryRequestDTO dto) {
        return categoryService.create(dto);
    }

    @GetMapping
    public List<CategoryResponseDTO> list() {
        return categoryService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
