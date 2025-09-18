package com.example.chatify.repository;

import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // ✅ Correct naming with recipient
    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(
            User sender, User recipient, User recipient2, User sender2
    );
}
