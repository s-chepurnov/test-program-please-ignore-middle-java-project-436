package com.shop.dto;

import com.shop.domain.Order;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        String id,
        String status,
        List<OrderItemDto> items,
        ShippingDetailsDto shipping,
        MoneyDto total,
        Instant createdAt
) {
    public static OrderDto from(Order order) {
        return new OrderDto(
                String.valueOf(order.id()),
                order.status(),
                order.items().stream().map(OrderItemDto::from).toList(),
                ShippingDetailsDto.from(order.shipping()),
                MoneyDto.from(order.total()),
                order.createdAt()
        );
    }
}
