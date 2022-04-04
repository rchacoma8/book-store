package com.adobe.bookstore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookOrderResourceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnOrdersIsEmptyWithoutHttpStatus() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/orders_without_http_status/", HashMap.class);

        assertEquals(result.get("orders"), Collections.EMPTY_LIST);
    }

    @Test
    public void shouldReturnOrdersIsEmptyWithHttpStatus() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/orders/", HashMap.class);

        assertEquals(result.get("orders"), Collections.EMPTY_LIST);
    }

    @Test
    @DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
    @Sql(statements = "INSERT INTO book_order (id, order_date, success) VALUES ('31d21723-c03b-4b18-a4de-b29d290febb1', '2022-04-04', true)")
    @Sql(statements = "INSERT INTO book_order_item (id, book_order_id, book_stock_id, quantity) VALUES (1, '31d21723-c03b-4b18-a4de-b29d290febb1', '14e05a52-eec7-4ff4-af1b-d6fd1cb90207', 1)")
    public void shouldReturnOrders() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/orders/", HashMap.class);
        assertEquals(result.toString(), "{orders=[{id=31d21723-c03b-4b18-a4de-b29d290febb1, orderDate=2022-04-04, success=true, bookOrderItems=[{id=1, bookStock=14e05a52-eec7-4ff4-af1b-d6fd1cb90207, quantity=1}]}]}");
    }

    

    @Test
    public void shouldReturnOrderUUID() throws JsonProcessingException {
        Map<String,List<HashMap<String, String>>> body = new HashMap<>();
        List<HashMap<String, String>> orderList = new ArrayList<>();
        HashMap<String, String> item = new HashMap<>();
        
        item.put("name", "14e05a52-eec7-4ff4-af1b-d6fd1cb90207");
        item.put("quantity", "2");
        orderList.add(item);

        item.put("name", "6fcf1723-e8a4-4e4a-b84b-f29d1f7393e6");
        item.put("quantity", "3");
        orderList.add(item);

        body.put("order",orderList);
        
        ObjectMapper objectMapper = new ObjectMapper();

        // RequestBuilder request =  mockMvc.perform(MockMvcRequestBuilders.post("/order")
        //             .contentType(MediaType.APPLICATION_JSON)
        //             .content(objectMapper.writeValueAsString(body))
        //             .accept(MediaType.APPLICATION_JSON));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/order")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);        
    }

}
