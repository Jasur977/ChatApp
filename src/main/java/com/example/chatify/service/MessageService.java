package com.example.chatify.service;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.repository.GroupChatRepository;
import com.example.chatify.repository.MessageRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final GroupChatRepository groupChatRepo;

    public MessageService(MessageRepository messageRepo, UserRepository userRepo, GroupChatRepository groupChatRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.groupChatRepo = groupChatRepo;
    }

    // === Direct messages ===
    public Message sendDirectMessage(Long senderId, Long receiverId, String content) {
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

    // === Group messages ===
    public Message sendGroupMessage(Long senderId, Long groupId, String content) {
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setGroupChat(group);
        message.setContent(content);

        return messageRepo.save(message);
    }

    public List<Message> getGroupMessages(Long groupId) {
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return messageRepo.findByGroupChatOrderByTimestampAsc(group);
    }
}
