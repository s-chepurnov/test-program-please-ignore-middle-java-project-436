package com.shop.controller;

import com.shop.dto.UserDto;
import com.shop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody CredentialsRequest request) {
        var user = userService.register(request.email(), request.password());
        return UserDto.from(user);
    }

    @PostMapping("/session")
    public UserDto login(@RequestBody CredentialsRequest request) {
        var user = userService.authenticate(request.email(), request.password());
        return UserDto.from(user);
    }

    @GetMapping("/me")
    public UserDto me(@RequestAttribute("userId") int userId) {
        return UserDto.from(userService.findById(userId));
    }

    public record CredentialsRequest(String email, String password) {}
}
