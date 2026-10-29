package com.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promos")
public class PromoController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(List.of(
                Map.of("id", 1, "title", "Скидка 20%", "description", "На первый заказ",
                        "image", "/assets/promo1.png", "link", "/catalog"),
                Map.of("id", 2, "title", "Бесплатная доставка", "description", "От 3000 ₽",
                        "image", "/assets/promo2.png", "link", "/delivery")
        ));
    }
}
