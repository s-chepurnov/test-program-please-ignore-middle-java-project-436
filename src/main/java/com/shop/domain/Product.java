package com.shop.domain;

public record Product(
        int id,
        String slug,
        String name,
        String description,
        Money price,
        int categoryId,
        String imageUrl,
        boolean available
) {}
