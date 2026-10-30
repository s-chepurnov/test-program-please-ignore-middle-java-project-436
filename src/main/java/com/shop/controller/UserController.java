package com.shop.controller;

import com.shop.domain.User;
import com.shop.dto.CredentialsRequest;
import com.shop.dto.UserDto;
import com.shop.exception.UnauthorizedException;
import com.shop.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto register(@Valid @RequestBody CredentialsRequest request) {
    var user = userService.register(request.email(), request.password());
    var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);
    return UserDto.from(user);
  }

  @PostMapping("/session")
  public UserDto login(@Valid @RequestBody CredentialsRequest request) {
    var user = userService.authenticate(request.email(), request.password());
    var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);
    return UserDto.from(user);
  }

  @GetMapping("/me")
  public UserDto me() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof User user)) {
      throw new UnauthorizedException("Not authenticated");
    }
    return UserDto.from(user);
  }
}
