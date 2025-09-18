package com.example.chatify.service;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.repository.GroupChatRepository;
import com.example.chatify.repository.MessageRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final GroupChatRepository groupChatRepository;

    public ChatService(UserRepository userRepository,
                       MessageRepository messageRepository,
                       GroupChatRepository groupChatRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.groupChatRepository = groupChatRepository;
    }

    // ✅ Direct message
    public Message sendDirect(Long fromUserId, Long toUserId, String content) {
        User sender = userRepository.findById(fromUserId).orElseThrow();
        User recipient = userRepository.findById(toUserId).orElseThrow();

        Message msg = new Message();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    // ✅ Group message
    public Message sendToGroup(Long fromUserId, Long groupId, String content) {
        User sender = userRepository.findById(fromUserId).orElseThrow();
        GroupChat group = groupChatRepository.findById(groupId).orElseThrow();

        Message msg = new Message();
        msg.setSender(sender);
        msg.setGroupChat(group);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    // ✅ Fetch direct chat history between two users
    public List<Message> directThread(Long userId1, Long userId2) {
        return messageRepository.findAll().stream()
                .filter(m ->
                        (m.getSender() != null && m.getRecipient() != null) &&
                                ((m.getSender().getId().equals(userId1) && m.getRecipient().getId().equals(userId2)) ||
                                        (m.getSender().getId().equals(userId2) && m.getRecipient().getId().equals(userId1)))
                )
                .toList();
    }

    // ✅ Fetch group chat history
    public List<Message> groupThread(Long groupId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getGroupChat() != null && m.getGroupChat().getId().equals(groupId))
                .toList();
    }

    // ✅ Create a new group
    public GroupChat createGroup(String name, Set<Long> memberIds) {
        Set<User> members = new HashSet<>();
        for (Long id : memberIds) {
            userRepository.findById(id).ifPresent(members::add);
        }

        GroupChat group = new GroupChat();
        group.setName(name);
        group.setMembers(members);

        return groupChatRepository.save(group);
    }
}
