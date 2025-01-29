package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CategoryDtoRequest;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDtoRequest categoryDtoRequest);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDtoRequest categoryDtoRequest);

    List<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long catsId);

    Category getCategoryByIdNotMapping(Long catId);
}
