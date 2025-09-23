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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepo;

    public UserController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    // ✅ Get current user
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("{\"error\":\"User not found\"}");
        }
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    // ✅ List all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> users = userService.listAll()
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // ✅ Add friend
    @PostMapping("/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long friendId, Authentication authentication) {
        User me = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        me.addFriend(friend);
        friend.addFriend(me);
        userRepo.save(me);
        userRepo.save(friend);

        return ResponseEntity.ok("✅ Friend added successfully!");
    }

    // ✅ Remove friend
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable Long friendId, Authentication authentication) {
        User me = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        me.removeFriend(friend);
        friend.removeFriend(me);
        userRepo.save(me);
        userRepo.save(friend);

        return ResponseEntity.ok("❌ Friend removed successfully!");
    }

    // ✅ List my friends
    @GetMapping("/friends")
    public ResponseEntity<Set<UserDTO.FriendDTO>> listFriends(Authentication authentication) {
        User me = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<UserDTO.FriendDTO> friends = me.getFriends().stream()
                .map(f -> new UserDTO.FriendDTO(f.getId(), f.getUsername(), f.getDisplayName()))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(friends);
    }
}
