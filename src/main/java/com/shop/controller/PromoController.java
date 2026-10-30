package com.shop.controller;

import com.shop.dto.PromoDto;
import com.shop.service.PromoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promos")
public class PromoController {

    private final PromoService promoService;

    public PromoController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping
    public List<PromoDto> list() {
        return promoService.findAll().stream()
                .map(PromoDto::from)
                .toList();
    }
}
