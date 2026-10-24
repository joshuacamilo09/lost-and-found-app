package com.lostandfound.service.category;

import com.lostandfound.dto.category.CategoryRequestDTO;
import com.lostandfound.dto.category.CategoryResponseDTO;
import com.lostandfound.model.Category;
import com.lostandfound.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.name());

        Category saved = categoryRepository.save(category);
        return new CategoryResponseDTO(saved.getId(), saved.getName());
    }

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getName()))
                .toList();
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
