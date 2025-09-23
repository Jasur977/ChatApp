package com.example.chatify.repository;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // ✅ Direct messages between two users
    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(
            User sender, User recipient, User recipient2, User sender2
    );

    // ✅ Group chat messages
    List<Message> findByGroupChatOrderByTimestampAsc(GroupChat groupChat);
}
