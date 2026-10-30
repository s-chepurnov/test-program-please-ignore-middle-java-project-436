package com.shop.repository;

import com.shop.domain.User;
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
        var list = jdbcTemplate.query(
                "select id, email, password_hash from users where email = ?",
                (rs, rowNum) -> new Object[]{
                        new User(rs.getInt("id"), rs.getString("email")),
                        rs.getString("password_hash")
                },
                email
        );
        return list.stream().map(row -> (User) row[0]).findFirst();
    }

    public Optional<User> findById(int id) {
        var list = jdbcTemplate.query(
                "select id, email from users where id = ?",
                MAPPER, id
        );
        return list.stream().findFirst();
    }

    public Optional<User> create(String email, String passwordHash) {
        var sql = """
                insert into users (email, password_hash) values (?, ?)
                on conflict (email) do nothing
                returning id, email
                """;
        var list = jdbcTemplate.query(sql, MAPPER, email, passwordHash);
        return list.stream().findFirst();
    }
}
