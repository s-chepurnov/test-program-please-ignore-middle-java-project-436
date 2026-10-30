package com.shop.repository;

import com.shop.domain.Money;
import com.shop.domain.Product;
import com.shop.domain.Promo;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PromoRepository {

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Promo> MAPPER =
      (rs, rowNum) -> {
        var product =
            new Product(
                rs.getInt("product_id"),
                rs.getString("product_slug"),
                rs.getString("product_name"),
                rs.getString("product_description"),
                new Money(rs.getInt("product_price_amount"), "RUB"),
                rs.getInt("product_category_id"),
                rs.getString("product_image_url"),
                rs.getBoolean("product_available"));
        return new Promo(rs.getInt("id"), rs.getString("title"), rs.getString("text"), product);
      };

  public PromoRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Promo> findAll() {
    var sql =
        """
                select m.id, m.title, m.text,
                       p.id as product_id,
                       p.slug as product_slug,
                       p.name as product_name,
                       p.description as product_description,
                       p.price_amount as product_price_amount,
                       p.category_id as product_category_id,
                       p.image_url as product_image_url,
                       p.available as product_available
                from promos m
                join products p on p.id = m.product_id
                order by m.id
                """;
    return jdbcTemplate.query(sql, MAPPER);
  }
}
