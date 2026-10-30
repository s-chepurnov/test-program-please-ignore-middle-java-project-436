package com.shop.service;

import com.shop.domain.User;
import com.shop.exception.EmailTakenException;
import com.shop.exception.InvalidCredentialsException;
import com.shop.exception.NotFoundException;
import com.shop.repository.UserRepository;
import com.shop.security.Pbkdf2Encoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final Pbkdf2Encoder passwordEncoder;

  public UserService(UserRepository userRepository, Pbkdf2Encoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  private String normalizeEmail(String email) {
    return email.strip().toLowerCase();
  }

  public User register(String rawEmail, String password) {
    String email = normalizeEmail(rawEmail);
    String hash = passwordEncoder.encode(password);
    User user = userRepository.create(email, hash);
    if (user == null) {
      throw new EmailTakenException("Email already taken");
    }
    return user;
  }

  public User authenticate(String rawEmail, String password) {
    String email = normalizeEmail(rawEmail);
    String hash = userRepository.findPasswordHash(email);
    if (hash == null || !passwordEncoder.matches(password, hash)) {
      throw new InvalidCredentialsException("Invalid email or password");
    }
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
  }

  public User findById(int id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
  }
}
