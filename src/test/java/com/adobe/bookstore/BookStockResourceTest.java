package com.adobe.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookStockResourceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookStockRepository bookStockRepository;

    @Test
    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @Sql(statements = "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67890', 'some book', 7)")
    public void shouldReturnCurrentStock() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/books_stock/12345-67890", BookStock.class);
        
        assertThat(result.getQuantity()).isEqualTo(7);
    }

    @Test
    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @Sql(statements = "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67890', 'some book', 7)")
    public void getBookById() {
        var result = bookStockRepository.findById("12345-67890");
        assertThat(result.get().getQuantity()).isEqualTo(7);
    }
}
