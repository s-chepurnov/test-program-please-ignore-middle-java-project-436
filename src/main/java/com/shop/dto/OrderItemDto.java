package com.shop.dto;

import com.shop.domain.OrderItem;

public record OrderItemDto(
    String productId, String productName, MoneyDto priceAtPurchase, int quantity) {
  public static OrderItemDto from(OrderItem item) {
    return new OrderItemDto(
        String.valueOf(item.productId()),
        item.productName(),
        MoneyDto.from(item.priceAtPurchase()),
        item.quantity());
  }
}
