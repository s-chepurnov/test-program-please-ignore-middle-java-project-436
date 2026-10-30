package com.shop.domain;

import java.time.Instant;
import java.util.List;

public record Order(
    int id,
    String status,
    List<OrderItem> items,
    ShippingDetails shipping,
    Money total,
    Instant createdAt) {}
