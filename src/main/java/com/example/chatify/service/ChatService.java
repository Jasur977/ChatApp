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
public class ChatService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final GroupChatRepository groupChatRepo;

    public ChatService(MessageRepository messageRepo, UserRepository userRepo, GroupChatRepository groupChatRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.groupChatRepo = groupChatRepo;
    }

    // ✅ Direct message
    public Message sendDirect(Long senderId, Long recipientId, String content) {
        User sender = userRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepo.findById(recipientId).orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);

        return messageRepo.save(message);
    }

    // ✅ Group message
    public Message sendToGroup(Long senderId, Long groupId, String content) {
        User sender = userRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        GroupChat groupChat = groupChatRepo.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setGroupChat(groupChat);
        message.setContent(content);

        return messageRepo.save(message);
    }
    public List<Message> directThread(Long userId, Long friendId) {
        User user = userRepo.findById(userId).orElseThrow();
        User friend = userRepo.findById(friendId).orElseThrow();

        return messageRepo.findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(user, friend, user, friend);
    }

    public List<Message> groupThread(Long groupId) {
        GroupChat groupChat = groupChatRepo.findById(groupId).orElseThrow();
        return messageRepo.findAll().stream()
                .filter(m -> m.getGroupChat() != null &&
                        m.getGroupChat().getId().equals(groupId))
                .toList();
    }
}
