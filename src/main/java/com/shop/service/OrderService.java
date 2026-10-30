package com.shop.service;

import com.shop.domain.*;
import com.shop.exception.AddressRequiredException;
import com.shop.exception.NotFoundException;
import com.shop.exception.UnavailableItemsException;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order create(int userId, List<OrderItemRequest> requested, ShippingDetails shipping) {
        if ("delivery".equals(shipping.method()) && (shipping.address() == null || shipping.address().isBlank())) {
            throw new AddressRequiredException("Address is required for delivery");
        }

        var resolved = new ArrayList<OrderItem>();
        var unavailable = new ArrayList<String>();
        int total = 0;

        for (var req : requested) {
            var product = productRepository.findById(req.productId()).orElse(null);
            if (product == null || !product.available()) {
                unavailable.add(String.valueOf(req.productId()));
                continue;
            }
            var item = new OrderItem(
                    product.id(),
                    product.name(),
                    product.price(),
                    req.quantity()
            );
            resolved.add(item);
            total += product.price().amount() * req.quantity();
        }

        if (!unavailable.isEmpty()) {
            throw new UnavailableItemsException("Some items are unavailable", unavailable);
        }

        var totalMoney = new Money(total, "RUB");
        return orderRepository.create(userId, resolved, shipping, totalMoney);
    }

    public List<Order> findByUser(int userId) {
        return orderRepository.findByUser(userId);
    }

    public Order findByIdAndUser(int orderId, int userId) {
        var order = orderRepository.findByIdAndUser(orderId, userId);
        if (order == null) {
            throw new NotFoundException("Order " + orderId + " not found");
        }
        return order;
    }

    public record OrderItemRequest(int productId, int quantity) {}
}
