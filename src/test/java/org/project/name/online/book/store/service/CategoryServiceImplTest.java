package org.project.name.online.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.name.online.book.store.dto.category.CategoryDto;
import org.project.name.online.book.store.dto.category.CreateCategoryRequestDto;
import org.project.name.online.book.store.exception.EntityNotFoundException;
import org.project.name.online.book.store.mapper.category.CategoryMapper;
import org.project.name.online.book.store.model.Category;
import org.project.name.online.book.store.repository.category.CategoryRepository;
import org.project.name.online.book.store.service.category.impl.CategoryServiceImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private static Category expectedCategory;
    private static CategoryDto expectedDto;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeAll
    static void beforeAll() {
        expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Test name");
        expectedCategory.setDescription("Test name descr.");
        expectedDto = new CategoryDto();
        expectedDto.setId(1L);
        expectedDto.setName("Test name");
        expectedDto.setDescription("Test name descr.");
    }

    @Test
    @DisplayName("Save category")
    void save_WithValidFields_ReturnsCategoryDto() {
        CreateCategoryRequestDto inputDto = createDtoRequest();
        Category category = createCategory();
        when(categoryMapper.toEntity(inputDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(expectedCategory);
        when(categoryMapper.toDto(expectedCategory)).thenReturn(expectedDto);

        CategoryDto actualDto = categoryService.save(inputDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actualDto));
        verify(categoryMapper, times(1)).toEntity(inputDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(expectedCategory);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Get a category by category id")
    void getById_WhichPersistInDb_ReturnsCategoryDto() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        when(categoryMapper.toDto(expectedCategory)).thenReturn(expectedDto);

        CategoryDto actualDto = categoryService.getById(categoryId);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actualDto));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(expectedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all books")
    void findAll_WhichPersistInDb_ReturnsListOfCategoryDto() {
        Category firstCategory = createFirstCategory();
        Category secondCategory = createSecondCategory();
        CategoryDto firstDto = cretateFirstCategoryDto();
        CategoryDto secondDto = createSecondCategoryDto();
        List<Category> categories = List.of(firstCategory, secondCategory);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Category> categoryPage = new PageImpl<>(
                categories, pageable, categories.size()
        );
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(firstCategory)).thenReturn(firstDto);
        when(categoryMapper.toDto(secondCategory)).thenReturn(secondDto);

        List<CategoryDto> expectedDtoList = List.of(firstDto, secondDto);
        List<CategoryDto> actualDtoList = categoryService.findAll(pageable);

        assertEquals(expectedDtoList.size(), actualDtoList.size());
        assertEquals(expectedDtoList, actualDtoList);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(firstCategory);
        verify(categoryMapper, times(1)).toDto(secondCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update a category by category id")
    void update_WhichPersistInDb_ReturnsCategoryDto() {
        Category expectedCategory = createExpectedCategory();
        CreateCategoryRequestDto inputDto = createUpdateDtoRequest();
        Category updatedCategory = createUpdatedCategory();
        CategoryDto expectedDto = createExpectedDto();
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        when(categoryMapper.mergeEntities(inputDto, expectedCategory)).thenReturn(updatedCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expectedDto);

        CategoryDto actualDto = categoryService.update(categoryId, inputDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedDto, actualDto));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).mergeEntities(inputDto, expectedCategory);
        verify(categoryRepository, times(1)).save(updatedCategory);
        verify(categoryMapper, times(1)).toDto(updatedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete a category by category id")
    void deleteById_WhichPersistInDb_ReturnsValid() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("getById. Throw EntityNotFoundException since the category doesn't exist")
    void getById_WithNonExistentCategory_ThrowsEntityNotFoundException() {
        Long nonExistentCategoryId = 100L;
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(nonExistentCategoryId)
        );
        String expected = "There is no category by id: " + nonExistentCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("findAll. Return empty list since there is no any categories in DB")
    void findAll_WhichNonPersistInDb_ReturnEmptyList() {
        List<Category> categories = List.of();
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Category> categoryPage = new PageImpl<>(
                categories, pageable, 0
        );
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        List<CategoryDto> actualDtoList = categoryService.findAll(pageable);

        assertEquals(0, actualDtoList.size());
        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("update. Throw EntityNotFoundException since the category doesn't exist")
    void update_ByNonPersistCategoryInDb_ThrowsEntityNotFoundException() {
        Long nonExistentCategoryId = 100L;
        CreateCategoryRequestDto inputDto = new CreateCategoryRequestDto();
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(nonExistentCategoryId, inputDto)
        );
        String expected = "There is no category by id: " + nonExistentCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("deleteById. Throw EntityNotFoundException since the category doesn't exist")
    void deleteById_ByNonPersistCategoryInDb_ThrowsEntityNotFoundException() {
        Long nonExistentCategoryId = 100L;
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deleteById(nonExistentCategoryId)
        );
        String expected = "There is no category by id: " + nonExistentCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(nonExistentCategoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private CreateCategoryRequestDto createDtoRequest() {
        CreateCategoryRequestDto inputDto = new CreateCategoryRequestDto();
        inputDto.setName("Test name");
        inputDto.setDescription("Test name descr.");
        return inputDto;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Test name");
        category.setDescription("Test name descr.");
        return category;
    }

    private Category createFirstCategory() {
        Category firstCategory = new Category();
        firstCategory.setId(1L);
        firstCategory.setName("Test name");
        firstCategory.setDescription("Test name descr.");
        return firstCategory;
    }

    private Category createSecondCategory() {
        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Test name2");
        secondCategory.setDescription("Test nameDescr.2");
        return secondCategory;
    }

    private CategoryDto cretateFirstCategoryDto() {
        CategoryDto firstDto = new CategoryDto();
        firstDto.setId(1L);
        firstDto.setName("Test name");
        firstDto.setDescription("Test name descr.");
        return firstDto;
    }

    private CategoryDto createSecondCategoryDto() {
        CategoryDto secondDto = new CategoryDto();
        secondDto.setId(2L);
        secondDto.setName("Test name2");
        secondDto.setDescription("Test name descr.2");
        return secondDto;
    }

    private Category createExpectedCategory() {
        Category expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Test name");
        expectedCategory.setDescription("Test name descr.");
        return expectedCategory;
    }

    private CreateCategoryRequestDto createUpdateDtoRequest() {
        CreateCategoryRequestDto inputDto = new CreateCategoryRequestDto();
        inputDto.setName("Update name");
        inputDto.setDescription("Update descr.");
        return inputDto;
    }

    private Category createUpdatedCategory() {
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Update name");
        updatedCategory.setDescription("Update descr.");
        return updatedCategory;
    }

    private CategoryDto createExpectedDto() {
        CategoryDto expectedDto = new CategoryDto();
        expectedDto.setId(1L);
        expectedDto.setName("Update name");
        expectedDto.setDescription("Update descr.");
        return expectedDto;
    }
}
