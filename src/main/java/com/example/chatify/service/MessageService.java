package com.example.chatify.service;

import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.repository.MessageRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    public MessageService(MessageRepository messageRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(receiver);
        message.setContent(content);

        return messageRepo.save(message);
    }

    public List<Message> getConversation(Long userId, Long friendId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepo.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        return messageRepo.findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(user, friend, user, friend);
    }
}
