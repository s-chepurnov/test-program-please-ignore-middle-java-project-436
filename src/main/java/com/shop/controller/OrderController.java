package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto create(@RequestBody CreateOrderRequest request,
                           @RequestAttribute("userId") int userId) {
        var items = request.items().stream()
                .map(i -> new OrderService.OrderItemRequest(i.productId(), i.quantity()))
                .toList();
        var order = orderService.create(userId, items, request.shipping());
        return OrderDto.from(order);
    }

    @GetMapping
    public List<OrderDto> list(@RequestAttribute("userId") int userId) {
        return orderService.findByUser(userId).stream().map(OrderDto::from).toList();
    }

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable String id, @RequestAttribute("userId") int userId) {
        int numericId;
        try {
            numericId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new com.shop.exception.NotFoundException("Order " + id + " not found");
        }
        return OrderDto.from(orderService.findByIdAndUser(numericId, userId));
    }

    public record CreateOrderRequest(List<ItemRequest> items, com.shop.domain.ShippingDetails shipping) {}
    public record ItemRequest(int productId, int quantity) {}
}
