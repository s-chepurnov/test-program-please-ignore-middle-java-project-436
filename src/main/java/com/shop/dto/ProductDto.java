package com.shop.dto;

import com.shop.domain.Product;

public record ProductDto(
    String id,
    String slug,
    String name,
    String description,
    MoneyDto price,
    String categoryId,
    String imageUrl,
    boolean available) {
  public static ProductDto from(Product product) {
    return new ProductDto(
        String.valueOf(product.id()),
        product.slug(),
        product.name(),
        product.description(),
        MoneyDto.from(product.price()),
        String.valueOf(product.categoryId()),
        product.imageUrl(),
        product.available());
  }
}
