package com.example.chatify.service;

import com.example.chatify.model.Post;
import com.example.chatify.model.User;
import com.example.chatify.repository.PostRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public PostService(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    public Post createPost(Long userId, String text) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setAuthor(author);
        post.setText(text);

        return postRepo.save(post);
    }

    public List<Post> getPostsByUser(Long userId) {
        return postRepo.findByAuthorId(userId);
    }

    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepo.delete(post);
    }
}
