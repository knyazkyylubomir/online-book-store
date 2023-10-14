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
import org.project.name.online.book.store.dto.book.BookDto;
import org.project.name.online.book.store.dto.book.CreateBookRequestDto;
import org.project.name.online.book.store.dto.book.UpdateBookRequestDto;
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
class BookControllerIntegrationTest {
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
                    new ClassPathResource("database/books/controller/"
                            + "add-categories-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/controller/"
                            + "add-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/controller/"
                            + "add-book_category-pairs-to-books_categories.sql")
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
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_RespondStatusCreated() throws Exception {
        CreateBookRequestDto requestDto = createBookRequest();
        BookDto expected = createExpectedDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Get a book by id")
    void getBookById_WhichExistInDb_RespondStatusOk() throws Exception {
        BookDto expected = createExpectedDtoFromDb();

        MvcResult result = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Get all books")
    void getAll_WhichExistInDb_RespondStatusOk() throws Exception {
        List<BookDto> expected = createExpectedDtoList();

        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class
        );

        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/books/controller/updateBook/"
            + "add-everything-for-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/controller/updateBook/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a book by id")
    void updateBook_ValidRequestDto_RespondStatusOk() throws Exception {
        UpdateBookRequestDto requestDto = createExpectedUpdateDto();
        BookDto expected = createUpdatedBookDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/books/3")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Search a book with search parameters")
    void searchBook_ValidSearchParameters_RespondStatusOk() throws Exception {
        List<BookDto> expected = createExpectedListAfterSearchBook();

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "Author2")
                        .param("prices", "97,98"))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class
        );

        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @Sql(scripts = "classpath:database/books/controller/deleteBook/"
            + "add-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/controller/deleteBook/"
            + "remove-everything-after-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete a book by id")
    void deleteBook_ValidRequestDto_RespondStatusOk() throws Exception {
        mockMvc.perform(delete("/books/3"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/3"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("createBook. Receive bad request status since dto isn't valid")
    void createBook_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("getBookById. Receive bad request status since id is less than 1")
    void getBookById_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(get("/books/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("updateBook. Receive bad request status since id is less than 1")
    void updateBook_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(put("/books/0"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("updateBook. Receive bad request status since dto isn't valid")
    void updateBook_NotValidRequestDto_RespondStatusBadRequest() throws Exception {
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setPrice(-10.99);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("deleteBook. Receive bad request status since id is less than 1")
    void deleteBook_NotValidId_RespondStatusBadRequest() throws Exception {
        mockMvc.perform(delete("/books/0"))
                .andExpect(status().isBadRequest());
    }

    private CreateBookRequestDto createBookRequest() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Title");
        requestDto.setAuthor("Author");
        requestDto.setIsbn("123456789");
        requestDto.setPrice(99.99);
        requestDto.setDescription("Descr.");
        requestDto.setCoverImage("Image");
        requestDto.setCategoryIds(List.of(1L));
        return requestDto;
    }

    private BookDto createExpectedDto() {
        BookDto expected = new BookDto();
        expected.setTitle("Title");
        expected.setAuthor("Author");
        expected.setIsbn("123456789");
        expected.setPrice(99.99);
        expected.setDescription("Descr.");
        expected.setCoverImage("Image");
        expected.setCategoryIds(List.of("Test name1"));
        return expected;
    }

    private BookDto createExpectedDtoFromDb() {
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Title1");
        expected.setAuthor("Author1");
        expected.setIsbn("123456789(1)");
        expected.setPrice(98.99);
        expected.setDescription("Descr.1");
        expected.setCoverImage("Image1");
        expected.setCategoryIds(List.of("Test name1"));
        return expected;
    }

    private List<BookDto> createExpectedDtoList() {
        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setTitle("Title1");
        firstBookDto.setAuthor("Author1");
        firstBookDto.setIsbn("123456789(1)");
        firstBookDto.setPrice(98.99);
        firstBookDto.setDescription("Descr.1");
        firstBookDto.setCoverImage("Image1");
        firstBookDto.setCategoryIds(List.of("Test name1"));
        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("Title2");
        secondBookDto.setAuthor("Author2");
        secondBookDto.setIsbn("123456789(2)");
        secondBookDto.setPrice(97.99);
        secondBookDto.setDescription("Descr.2");
        secondBookDto.setCoverImage("Image2");
        secondBookDto.setCategoryIds(List.of("Test name2"));
        return List.of(firstBookDto, secondBookDto);
    }

    private UpdateBookRequestDto createExpectedUpdateDto() {
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setTitle("Update title");
        requestDto.setAuthor("Update author");
        requestDto.setIsbn("Update isbn");
        requestDto.setPrice(34.54);
        requestDto.setDescription("Update descr.");
        requestDto.setCoverImage("Update image");
        requestDto.setCategoryIds(List.of(3L));
        return requestDto;
    }

    private BookDto createUpdatedBookDto() {
        BookDto expected = new BookDto();
        expected.setId(3L);
        expected.setTitle("Update title");
        expected.setAuthor("Update author");
        expected.setIsbn("Update isbn");
        expected.setPrice(34.54);
        expected.setDescription("Update descr.");
        expected.setCoverImage("Update image");
        expected.setCategoryIds(List.of("Test name for update"));
        return expected;
    }

    private List<BookDto> createExpectedListAfterSearchBook() {
        BookDto expectedDto = new BookDto();
        expectedDto.setId(2L);
        expectedDto.setTitle("Title2");
        expectedDto.setAuthor("Author2");
        expectedDto.setIsbn("123456789(2)");
        expectedDto.setPrice(97.99);
        expectedDto.setDescription("Descr.2");
        expectedDto.setCoverImage("Image2");
        expectedDto.setCategoryIds(List.of("Test name2"));
        return List.of(expectedDto);
    }
}
