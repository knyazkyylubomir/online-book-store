package org.project.name.online.book.store.service.category.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.dto.category.CategoryDto;
import org.project.name.online.book.store.dto.category.CreateCategoryRequestDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.category.CategoryMapper;
import org.project.name.online.book.store.model.Category;
import org.project.name.online.book.store.repository.category.CategoryRepository;
import org.project.name.online.book.store.service.category.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("There is no category by: " + id));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto categoryDto) {
        Category categoryById = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no category by: " + id));
        Category category = categoryMapper.mergeEntities(categoryDto, categoryById);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no category by id: " + id));
        categoryRepository.deleteById(id);
    }
}
