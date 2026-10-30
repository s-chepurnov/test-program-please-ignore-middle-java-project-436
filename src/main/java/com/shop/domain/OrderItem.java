package com.shop.domain;

public record OrderItem(
        int productId,
        String productName,
        Money priceAtPurchase,
        int quantity
) {}
