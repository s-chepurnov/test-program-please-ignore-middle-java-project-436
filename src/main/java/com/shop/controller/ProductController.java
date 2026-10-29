package com.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(List.of(
                Map.of("id", 101, "name", "Тестовый товар 1", "price", 990.00,
                        "categoryId", 1, "image", "/assets/placeholder.png"),
                Map.of("id", 102, "name", "Тестовый товар 2", "price", 1990.00,
                        "categoryId", 2, "image", "/assets/placeholder.png")
        ));
    }
}
