package com.example.chatify.service;

import com.example.chatify.model.User;
import com.example.chatify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Register new user
    public User register(String username, String password, String displayName) {
        User user = new User(
                username,
                passwordEncoder.encode(password), // hash password
                displayName
        );
        return userRepo.save(user);
    }

    // ✅ Return all users as entities (controller converts to DTOs)
    public List<User> listAll() {
        return userRepo.findAll();
    }

    // ✅ Find a user by ID
    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
