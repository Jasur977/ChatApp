package com.example.chatify.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    private Set<User> members = new HashSet<>();

    // Constructors
    public GroupChat() {}
    public GroupChat(Long id, String name, Set<User> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }
}
