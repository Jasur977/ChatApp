package com.example.chatify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore // 🚨 hides password in JSON responses
    @Column(nullable = false)
    private String password;

    private String displayName;

    // ✅ Self-referencing Many-to-Many for friends
    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @JsonIgnore // ⚠️ prevent infinite recursion (user -> friend -> user -> …)
    private Set<User> friends = new HashSet<>();

    // ✅ No-args constructor required for JPA + JSON deserialization
    public User() {}

    // Convenience constructor
    public User(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() {
        return displayName != null ? displayName : username;
    }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Set<User> getFriends() { return friends; }
    public void setFriends(Set<User> friends) { this.friends = friends; }

    // Convenience methods
    public void addFriend(User friend) {
        this.friends.add(friend);
        friend.getFriends().add(this); // ensure bidirectional
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
        friend.getFriends().remove(this);
    }
}
