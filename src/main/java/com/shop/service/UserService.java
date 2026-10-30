package com.shop.service;

import com.shop.domain.User;
import com.shop.exception.EmailTakenException;
import com.shop.exception.InvalidCredentialsException;
import com.shop.exception.NotFoundException;
import com.shop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String password) {
        var created = userRepository.create(email, password);
        return created.orElseThrow(() -> new EmailTakenException("Email already taken"));
    }

    public User authenticate(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        return user;
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }
}
