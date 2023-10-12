package org.project.name.online.book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.name.online.book.store.dto.book.BookDtoWithoutCategoryIds;
import org.project.name.online.book.store.dto.category.CategoryDto;
import org.project.name.online.book.store.dto.category.CreateCategoryRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/controller/"
                            + "add-categories-to-categories.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/controller/"
                            + "remove-everything-after-tests.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_RespondStatusCreated() throws Exception {
        CreateCategoryRequestDto requestDto = createCategoryDto();
        CategoryDto expected = createExpectedDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Get a category by id")
    void getCategoryById_WhichExistInDb_RespondStatusOk() throws Exception {
        CategoryDto expected = createExpectedDtoFromDb();

        MvcResult result = mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Get all categories")
    void getAll_WhichExistInDb_RespondStatusOk() throws Exception {
        List<CategoryDto> expected = createExpectedDtoList();

        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto[].class
        );

        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/categories/controller/updateCategory/"
            + "add-category-to-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/controller/updateCategory/"
            + "remove-category-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a category by id")
    void updateCategory_ValidRequestDto_RespondStatusOk() throws Exception {
        CreateCategoryRequestDto requestDto = createUpdateRequestDto();
        CategoryDto expected = createUpdatedDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/categories/3")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/categories/controller/deleteCategory/"
            + "add-category-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete category by id")
    void deleteCategory_ValidRequestDto_RespondStatusOk() throws Exception {
        mockMvc.perform(delete("/categories/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories/3"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @Sql(scripts = "classpath:database/categories/controller/getBooksByCategoryId/"
            + "add-everything-for-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/controller/getBooksByCategoryId/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_WhichExistInDb_RespondStatusOk() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = createListOfDtoWithoutCategoryId();

        MvcResult result = mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class
        );

        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("createCategory. Receive bad request status since dto isn't valid")
    void createCategory_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("getCategoryById. Receive bad request status since id is less than 1")
    void getCategoryById_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(get("/categories/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("updateCategory. Receive bad request status since id is less than 1")
    void updateCategory_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(put("/categories/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("updateCategory. Receive bad request status since dto isn't valid")
    void updateCategory_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("deleteCategory. Receive bad request status since id is less than 1")
    void deleteCategory_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(delete("/categories/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("getBooksByCategoryId. Receive bad request status since id is less than 1")
    void getBooksByCategoryId_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(get("/categories/0/books"))
                .andExpect(status().isBadRequest());
    }

    private CreateCategoryRequestDto createCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Test name");
        requestDto.setDescription("Test name descr.");
        return requestDto;
    }

    private CategoryDto createExpectedDto() {
        CategoryDto expected = new CategoryDto();
        expected.setName("Test name");
        expected.setDescription("Test name descr.");
        return expected;
    }

    private CategoryDto createExpectedDtoFromDb() {
        CategoryDto expected = new CategoryDto();
        expected.setId(1L);
        expected.setName("Test name1");
        expected.setDescription("Test name descr.1");
        return expected;
    }

    private List<CategoryDto> createExpectedDtoList() {
        CategoryDto firstDto = new CategoryDto();
        firstDto.setId(1L);
        firstDto.setName("Test name1");
        firstDto.setDescription("Test name descr.1");
        CategoryDto secondDto = new CategoryDto();
        secondDto.setId(2L);
        secondDto.setName("Test name2");
        secondDto.setDescription("Test name descr.2");
        return List.of(firstDto, secondDto);
    }

    private CreateCategoryRequestDto createUpdateRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Update name");
        requestDto.setDescription("Update descr.");
        return requestDto;
    }

    private CategoryDto createUpdatedDto() {
        CategoryDto expected = new CategoryDto();
        expected.setId(3L);
        expected.setName("Update name");
        expected.setDescription("Update descr.");
        return expected;
    }

    private List<BookDtoWithoutCategoryIds> createListOfDtoWithoutCategoryId() {
        BookDtoWithoutCategoryIds firstBookDto = new BookDtoWithoutCategoryIds();
        firstBookDto.setId(1L);
        firstBookDto.setTitle("Title1");
        firstBookDto.setAuthor("Author1");
        firstBookDto.setIsbn("123456789(1)");
        firstBookDto.setPrice(98.99);
        firstBookDto.setDescription("Descr.1");
        firstBookDto.setCoverImage("Image1");
        BookDtoWithoutCategoryIds secondBookDto = new BookDtoWithoutCategoryIds();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("Title2");
        secondBookDto.setAuthor("Author2");
        secondBookDto.setIsbn("123456789(2)");
        secondBookDto.setPrice(97.99);
        secondBookDto.setDescription("Descr.2");
        secondBookDto.setCoverImage("Image2");
        return List.of(firstBookDto, secondBookDto);
    }
}
