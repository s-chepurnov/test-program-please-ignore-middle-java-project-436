package com.shop.repository;

import com.shop.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate txTemplate;

    private static final RowMapper<OrderItem> ITEM_MAPPER = (rs, rowNum) ->
            new OrderItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    new Money(rs.getInt("price_amount"), "RUB"),
                    rs.getInt("quantity")
            );

    public OrderRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }

    public Order create(int userId, List<OrderItem> items, ShippingDetails shipping, Money total) {
        return txTemplate.execute(status -> {
            var orderId = jdbcTemplate.queryForObject("""
                    insert into orders (user_id, shipping_method, recipient_name, phone, address, total_amount)
                    values (?, ?, ?, ?, ?, ?) returning id
                    """, Integer.class,
                    userId, shipping.method(), shipping.recipientName(), shipping.phone(),
                    shipping.address(), total.amount());

            for (var item : items) {
                jdbcTemplate.update("""
                        insert into order_items (order_id, product_id, product_name, price_amount, quantity)
                        values (?, ?, ?, ?, ?)
                        """, orderId, item.productId(), item.productName(),
                        item.priceAtPurchase().amount(), item.quantity());
            }

            return new Order(
                    orderId,
                    "paid",
                    items,
                    shipping,
                    total,
                    Instant.now()
            );
        });
    }

    public List<Order> findByUser(int userId) {
        var orders = jdbcTemplate.query("""
                select id, status, shipping_method, recipient_name, phone, address, total_amount, created_at
                from orders where user_id = ? order by id desc
                """, (rs, rowNum) -> new Object[]{
                rs.getInt("id"),
                rs.getString("status"),
                new ShippingDetails(
                        rs.getString("shipping_method"),
                        rs.getString("recipient_name"),
                        rs.getString("phone"),
                        rs.getString("address")
                ),
                new Money(rs.getInt("total_amount"), "RUB"),
                rs.getTimestamp("created_at").toInstant()
        }, userId);

        var result = new ArrayList<Order>();
        for (var row : orders) {
            var id = (int) row[0];
            var items = findItems(id);
            result.add(new Order(
                    id,
                    (String) row[1],
                    items,
                    (ShippingDetails) row[2],
                    (Money) row[3],
                    (Instant) row[4]
            ));
        }
        return result;
    }

    public Order findByIdAndUser(int orderId, int userId) {
        var orders = jdbcTemplate.query("""
                select id, status, shipping_method, recipient_name, phone, address, total_amount, created_at
                from orders where id = ? and user_id = ?
                """, (rs, rowNum) -> new Object[]{
                rs.getInt("id"),
                rs.getString("status"),
                new ShippingDetails(
                        rs.getString("shipping_method"),
                        rs.getString("recipient_name"),
                        rs.getString("phone"),
                        rs.getString("address")
                ),
                new Money(rs.getInt("total_amount"), "RUB"),
                rs.getTimestamp("created_at").toInstant()
        }, orderId, userId);

        if (orders.isEmpty()) {
            return null;
        }
        var row = orders.get(0);
        var id = (int) row[0];
        var items = findItems(id);
        return new Order(
                id,
                (String) row[1],
                items,
                (ShippingDetails) row[2],
                (Money) row[3],
                (Instant) row[4]
        );
    }

    private List<OrderItem> findItems(int orderId) {
        return jdbcTemplate.query("""
                select product_id, product_name, price_amount, quantity
                from order_items where order_id = ? order by id
                """, ITEM_MAPPER, orderId);
    }
}
