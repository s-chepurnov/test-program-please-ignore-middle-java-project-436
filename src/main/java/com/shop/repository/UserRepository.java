package com.shop.repository;

import com.shop.domain.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> MAPPER = (rs, rowNum) ->
            new User(rs.getInt("id"), rs.getString("email"));

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("select id, email from users where email = ?", MAPPER, email)
                .stream().findFirst();
    }

    public Optional<User> findById(int id) {
        return jdbcTemplate.query("select id, email from users where id = ?", MAPPER, id)
                .stream().findFirst();
    }

    public String findPasswordHash(String email) {
        var list = jdbcTemplate.queryForList("select password_hash from users where email = ?", String.class, email);
        return list.isEmpty() ? null : list.get(0);
    }

    public User create(String email, String passwordHash) {
        try {
            return jdbcTemplate.queryForObject(
                    "insert into users (email, password_hash) values (?, ?) returning id, email",
                    MAPPER, email, passwordHash);
        } catch (DuplicateKeyException e) {
            return null;
        }
    }
}
