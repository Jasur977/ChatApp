package com.example.chatify.controller;

import com.example.chatify.model.GroupChat;
import com.example.chatify.model.User;
import com.example.chatify.service.GroupChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupchats")
@CrossOrigin(origins = "http://localhost:5173") // allow React dev server
public class GroupChatController {

    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    // ✅ Create a group (creator auto-added as member)
    @PostMapping("/create")
    public GroupChat createGroup(@RequestBody Map<String, Object> payload,
                                 Authentication authentication) {
        String name = (String) payload.get("name");
        return groupChatService.createGroup(name, authentication.getName());
    }

    // ✅ Get all groups (visible to everyone)
    @GetMapping("/all")
    public List<GroupChat> getAllGroups() {
        return groupChatService.getAllGroups();
    }

    // ✅ Get only groups the logged-in user belongs to
    @GetMapping("/mine")
    public List<GroupChat> getMyGroups(Authentication authentication) {
        return groupChatService.getMyGroups(authentication.getName());
    }

    // ✅ Join a group
    @PostMapping("/{groupId}/join")
    public GroupChat joinGroup(@PathVariable Long groupId,
                               Authentication authentication) {
        return groupChatService.joinGroup(groupId, authentication.getName());
    }
}
