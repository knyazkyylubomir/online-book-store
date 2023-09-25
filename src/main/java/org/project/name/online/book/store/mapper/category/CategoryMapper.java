package org.project.name.online.book.store.mapper.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.name.online.book.store.config.MapperConfig;
import org.project.name.online.book.store.dto.category.CategoryDto;
import org.project.name.online.book.store.dto.category.CreateCategoryRequestDto;
import org.project.name.online.book.store.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto categoryDto);

    Category mergeEntities(CreateCategoryRequestDto categoryDto, @MappingTarget Category category);
}
