package com.example.chatify.controller;

import com.example.chatify.model.Post;
import com.example.chatify.model.User;
import com.example.chatify.repository.PostRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public PostController(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    // ✅ Create a post
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestParam Long userId, @RequestParam String text) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setText(text);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepo.save(post);
        return ResponseEntity.ok(savedPost);
    }

    // ✅ Get all posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepo.findAll());
    }

    // ✅ Get posts by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(postRepo.findByAuthor(user));
    }

    // ✅ Delete a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        if (!postRepo.existsById(postId)) {
            return ResponseEntity.badRequest().body("❌ Post not found");
        }
        postRepo.deleteById(postId);
        return ResponseEntity.ok("🗑️ Post deleted successfully");
    }
}
