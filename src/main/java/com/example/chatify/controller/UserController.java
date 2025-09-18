package com.example.chatify.controller;

import com.example.chatify.dto.UserDTO;
import com.example.chatify.model.User;
import com.example.chatify.repository.UserRepository;
import com.example.chatify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepo;

    public UserController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    // ✅ Register a new user (still using User entity for input)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"error\":\"User already exists!\"}");
        }

        User savedUser = userService.register(
                user.getUsername(),
                user.getPassword(),
                user.getDisplayName()
        );

        return ResponseEntity.ok(UserDTO.fromEntity(savedUser));
    }

    // ✅ List all users (as DTOs)
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> users = userService.listAll()
                .stream() // this must be List<User>
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

    // ✅ List user’s friends as FriendDTOs
    @GetMapping("/{userId}/friends")
    public ResponseEntity<Set<UserDTO.FriendDTO>> listFriends(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<UserDTO.FriendDTO> friends = user.getFriends().stream()
                .map(f -> new UserDTO.FriendDTO(f.getId(), f.getUsername(), f.getDisplayName()))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(friends);
    }
}
