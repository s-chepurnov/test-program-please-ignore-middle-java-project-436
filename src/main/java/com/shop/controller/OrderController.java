package com.shop.controller;

import com.shop.domain.ShippingDetails;
import com.shop.domain.User;
import com.shop.dto.CreateOrderRequest;
import com.shop.dto.OrderDto;
import com.shop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                           @AuthenticationPrincipal User user) {

        var shipping = new ShippingDetails(
                request.shipping().method(),
                request.shipping().recipientName(),
                request.shipping().phone(),
                request.shipping().address()
        );

        var order = orderService.create(user.id(), request.items(), shipping);
        return OrderDto.from(order);
    }

    @GetMapping
    public List<OrderDto> list(@AuthenticationPrincipal User user) {
        return orderService.findByUser(user.id()).stream()
                .map(OrderDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable int id, @AuthenticationPrincipal User user) {
        return OrderDto.from(orderService.findByIdAndUser(id, user.id()));
    }
}
