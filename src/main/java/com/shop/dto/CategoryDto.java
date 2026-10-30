package com.shop.dto;

import com.shop.domain.Category;

public record CategoryDto(
        String id,
        String slug,
        String name
) {
    public static CategoryDto from(Category category) {
        return new CategoryDto(
                String.valueOf(category.id()),
                category.slug(),
                category.name()
        );
    }
}
