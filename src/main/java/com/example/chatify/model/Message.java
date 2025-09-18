package com.example.chatify.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime sentAt;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    @ManyToOne
    private GroupChat groupChat;

    // Constructors
    public Message() {}
    public Message(Long id, String content, LocalDateTime sentAt, User sender, User recipient, GroupChat groupChat) {
        this.id = id;
        this.content = content;
        this.sentAt = sentAt;
        this.sender = sender;
        this.recipient = recipient;
        this.groupChat = groupChat;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public GroupChat getGroupChat() { return groupChat; }
    public void setGroupChat(GroupChat groupChat) { this.groupChat = groupChat; }
}
