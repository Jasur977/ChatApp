package com.example.chatify.service;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.User;
import com.example.chatify.repository.GroupChatRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupChatService {

    private final GroupChatRepository groupRepo;
    private final UserRepository userRepo;

    public GroupChatService(GroupChatRepository groupRepo, UserRepository userRepo) {
        this.groupRepo = groupRepo;
        this.userRepo = userRepo;
    }

    public GroupChat createGroup(String name, String username) {
        User creator = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupChat group = new GroupChat();
        group.setName(name);
        group.getMembers().add(creator);

        return groupRepo.save(group);
    }

    public List<GroupChat> getAllGroups() {
        return groupRepo.findAll();
    }

    public List<GroupChat> getMyGroups(String username) {
        User me = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return groupRepo.findAll().stream()
                .filter(g -> g.getMembers().contains(me))
                .toList();
    }

    public GroupChat joinGroup(Long groupId, String username) {
        User me = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupChat group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.getMembers().add(me);
        return groupRepo.save(group);
    }
}
