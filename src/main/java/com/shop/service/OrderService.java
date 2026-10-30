package com.shop.service;

import com.shop.domain.Money;
import com.shop.domain.Order;
import com.shop.domain.OrderItem;
import com.shop.domain.Product;
import com.shop.domain.ShippingDetails;
import com.shop.dto.ItemRequest;
import com.shop.exception.AddressRequiredException;
import com.shop.exception.NotFoundException;
import com.shop.exception.UnavailableItemsException;
import com.shop.repository.OrderRepository;
import com.shop.repository.ProductRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
  }

  public Order create(int userId, List<ItemRequest> requestedItems, ShippingDetails shipping) {
    if ("delivery".equals(shipping.method())
        && (shipping.address() == null || shipping.address().isBlank())) {
      throw new AddressRequiredException("Address is required for delivery");
    }

    Map<Integer, Integer> quantities = new HashMap<>();
    for (var item : requestedItems) {
      quantities.merge(item.productId(), item.quantity(), Integer::sum);
    }

    var productIds = new ArrayList<>(quantities.keySet());
    Map<Integer, Product> products = productRepository.findByIds(productIds);

    var resolvedItems = new ArrayList<OrderItem>();
    var unavailable = new ArrayList<String>();
    int total = 0;

    for (var entry : quantities.entrySet()) {
      var productId = entry.getKey();
      var quantity = entry.getValue();
      var product = products.get(productId);

      if (product == null || !product.available()) {
        unavailable.add(String.valueOf(productId));
        continue;
      }

      var orderItem = new OrderItem(product.id(), product.name(), product.price(), quantity);
      resolvedItems.add(orderItem);
      total += product.price().amount() * quantity;
    }

    if (!unavailable.isEmpty()) {
      throw new UnavailableItemsException("Some items are unavailable", unavailable);
    }

    var totalMoney = new Money(total, "RUB");
    return orderRepository.create(userId, resolvedItems, shipping, totalMoney);
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
}
