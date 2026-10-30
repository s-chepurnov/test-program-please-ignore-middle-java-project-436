package com.shop.dto;

import com.shop.domain.User;

public record UserDto(
        String id,
        String email
) {
    public static UserDto from(User user) {
        return new UserDto(String.valueOf(user.id()), user.email());
    }
}
