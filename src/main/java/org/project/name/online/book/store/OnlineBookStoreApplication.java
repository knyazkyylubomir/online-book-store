package org.project.name.online.book.store;

import java.math.BigDecimal;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book ivanhoe = new Book();
            ivanhoe.setAuthor("Walter Scott");
            ivanhoe.setTitle("Ivanhoe");
            ivanhoe.setDescription("A historical novel");
            ivanhoe.setPrice(BigDecimal.valueOf(199));

            bookService.save(ivanhoe);

            Book harryPotter = new Book();
            harryPotter.setAuthor("J. K. Rowling");
            harryPotter.setTitle("Harry Potter");
            harryPotter.setDescription("A fantasy novel");
            harryPotter.setPrice(BigDecimal.valueOf(299));

            bookService.save(harryPotter);

            System.out.println(bookService.findAll());
        };
    }
}
