package com.shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PromoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promos_shouldReturnAtLeastTwoBlocksWithFullAvailableProduct() throws Exception {
        mockMvc.perform(get("/api/promos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].title").exists())
                .andExpect(jsonPath("$[*].text").exists())
                .andExpect(jsonPath("$[*].product").exists())
                .andExpect(jsonPath("$[*].product.id").exists())
                .andExpect(jsonPath("$[*].product.slug").exists())
                .andExpect(jsonPath("$[*].product.name").exists())
                .andExpect(jsonPath("$[*].product.description").exists())
                .andExpect(jsonPath("$[*].product.price").exists())
                .andExpect(jsonPath("$[*].product.price.amount").exists())
                .andExpect(jsonPath("$[*].product.price.currency").exists())
                .andExpect(jsonPath("$[*].product.categoryId").exists())
                .andExpect(jsonPath("$[*].product.available").exists())
                .andExpect(jsonPath("$[*].product.available", everyItem(is(true))));
    }
}
