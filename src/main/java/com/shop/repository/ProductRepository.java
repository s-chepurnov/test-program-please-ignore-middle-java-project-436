package com.shop.repository;

import com.shop.domain.Money;
import com.shop.domain.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String PRODUCT_COLUMNS =
            "p.id, p.slug, p.name, p.description, p.price_amount, p.category_id, p.image_url, p.available";
    private static final String PRODUCTS_FROM =
            "from products p join categories c on c.id = p.category_id";

    private static final RowMapper<Product> MAPPER = (rs, rowNum) ->
            new Product(
                    rs.getInt("id"),
                    rs.getString("slug"),
                    rs.getString("name"),
                    rs.getString("description"),
                    new Money(rs.getInt("price_amount"), "RUB"),
                    rs.getInt("category_id"),
                    rs.getString("image_url"),
                    rs.getBoolean("available")
            );

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record Page(List<Product> items, int total) {}

    public Page findPage(String category, String search, Integer priceMin, Integer priceMax,
                         Boolean available, int page, int perPage) {
        var conditions = new ArrayList<String>();
        var params = new ArrayList<Object>();
        conditions.add("true");

        if (category != null && !category.isBlank()) {
            conditions.add("c.slug = ?");
            params.add(category);
        }
        if (search != null && !search.isBlank()) {
            conditions.add("p.name ilike ?");
            params.add("%" + search + "%");
        }
        if (priceMin != null) {
            conditions.add("p.price_amount >= ?");
            params.add(priceMin);
        }
        if (priceMax != null) {
            conditions.add("p.price_amount <= ?");
            params.add(priceMax);
        }
        if (available != null) {
            conditions.add("p.available = ?");
            params.add(available);
        }

        var where = String.join(" and ", conditions);
        var total = jdbcTemplate.queryForObject(
                "select count(*) " + PRODUCTS_FROM + " where " + where,
                Integer.class, params.toArray());
        var safeTotal = total != null ? total : 0;

        var pageParams = new ArrayList<>(params);
        pageParams.add(perPage);
        pageParams.add((page - 1) * perPage);

        var items = jdbcTemplate.query(
                "select " + PRODUCT_COLUMNS + " " + PRODUCTS_FROM + " where " + where
                        + " order by p.id limit ? offset ?",
                MAPPER, pageParams.toArray());

        return new Page(items, safeTotal);
    }

    public Optional<Product> findById(int id) {
        var list = jdbcTemplate.query(
                "select " + PRODUCT_COLUMNS + " from products p where p.id = ?",
                MAPPER, id);
        return list.stream().findFirst();
    }
}
