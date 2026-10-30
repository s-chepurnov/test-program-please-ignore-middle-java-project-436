package com.shop.repository;

import com.shop.domain.Category;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository {

  private final JdbcTemplate jdbcTemplate;

  private static final RowMapper<Category> MAPPER =
      (rs, rowNum) -> new Category(rs.getInt("id"), rs.getString("slug"), rs.getString("name"));

  public CategoryRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Category> findAll() {
    return jdbcTemplate.query("select id, slug, name from categories order by id", MAPPER);
  }
}
