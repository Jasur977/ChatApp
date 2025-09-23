package com.example.chatify.repository;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Direct conversation (both ways)
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender = :user AND m.recipient = :friend) " +
            "   OR (m.sender = :friend AND m.recipient = :user) " +
            "ORDER BY m.timestamp ASC")
    List<Message> getConversation(User user, User friend);

    // Group conversation
    @Query("SELECT m FROM Message m " +
            "WHERE m.groupChat = :group " +
            "ORDER BY m.timestamp ASC")
    List<Message> getGroupMessages(GroupChat group);
}
