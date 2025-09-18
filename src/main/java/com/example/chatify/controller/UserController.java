package com.example.chatify.controller;

import com.example.chatify.dto.UserDTO;
import com.example.chatify.model.User;
import com.example.chatify.repository.UserRepository;
import com.example.chatify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users") // ✅ prefixed with /api


public class UserController {

    private final UserService userService;
    private final UserRepository userRepo;

    public UserController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    // ✅ Get current logged-in user (from JWT)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName(); // Extracted from JWT
        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("{\"error\":\"User not found\"}");
        }

        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    // ✅ List all users (as DTOs)
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> users = userService.listAll()
                .stream()
                .map(UserDTO::fromEntity) // converts User -> UserDTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // ✅ Add a friend
    @PostMapping("/{userId}/add-friend/{friendId}")
    public ResponseEntity<String> addFriend(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        user.addFriend(friend);
        userRepo.save(user);

        return ResponseEntity.ok("✅ Friend added successfully!");
    }

    // ✅ Remove a friend
    @DeleteMapping("/{userId}/remove-friend/{friendId}")
    public ResponseEntity<String> removeFriend(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        user.removeFriend(friend);
        userRepo.save(user);

        return ResponseEntity.ok("❌ Friend removed successfully!");
    }

    // ✅ List user’s friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<Set<UserDTO.FriendDTO>> listFriends(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<UserDTO.FriendDTO> friends = user.getFriends().stream()
                .map(f -> new UserDTO.FriendDTO(f.getId(), f.getUsername(), f.getDisplayName()))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(friends);
    }
}
