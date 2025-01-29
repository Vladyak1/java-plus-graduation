package ru.practicum.dto;

import org.mapstruct.Mapper;
import ru.practicum.model.Category;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category toCategory(CategoryDtoRequest categoryDtoRequest);

    CategoryDto toCategoryDto(Category category);
}
