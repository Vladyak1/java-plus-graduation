package category.service;

import org.springframework.data.domain.Pageable;
import category.dto.CategoryDto;
import category.dto.CategoryDtoRequest;
import category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDtoRequest categoryDtoRequest);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDtoRequest categoryDtoRequest);

    List<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long catsId);

    Category getCategoryByIdNotMapping(Long catId);
}
