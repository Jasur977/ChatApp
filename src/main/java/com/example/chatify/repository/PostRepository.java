package com.example.chatify.repository;

import com.example.chatify.model.Post;
import com.example.chatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);

    List<Post> findByAuthorId(Long authorId);
}
