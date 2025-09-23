package com.example.chatify.controller;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.User;
import com.example.chatify.repository.GroupChatRepository;
import com.example.chatify.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupchats")
@CrossOrigin(origins = "http://localhost:5173") // allow React dev server
public class GroupChatController {

    private final GroupChatRepository groupRepo;
    private final UserRepository userRepo;

    public GroupChatController(GroupChatRepository groupRepo, UserRepository userRepo) {
        this.groupRepo = groupRepo;
        this.userRepo = userRepo;
    }

    // ✅ Create a group (creator auto-added as member)
    @PostMapping("/create")
    public GroupChat createGroup(@RequestBody Map<String, Object> payload,
                                 Authentication authentication) {
        String name = (String) payload.get("name");

        User creator = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupChat group = new GroupChat();
        group.setName(name);
        group.getMembers().add(creator);

        return groupRepo.save(group);
    }

    // ✅ Get all groups (optional: replace with "mine")
    @GetMapping
    public List<GroupChat> getAllGroups() {
        return groupRepo.findAll();
    }

    // ✅ Get only groups the logged-in user belongs to
    @GetMapping("/mine")
    public List<GroupChat> getMyGroups(Authentication authentication) {
        User me = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return groupRepo.findAll().stream()
                .filter(g -> g.getMembers().contains(me))
                .toList();
    }

    // ✅ Join a group
    @PostMapping("/{groupId}/join")
    public GroupChat joinGroup(@PathVariable Long groupId,
                               Authentication authentication) {
        User me = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupChat group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.getMembers().add(me);
        return groupRepo.save(group);
    }
}
