package com.example.chatify.controller;

import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.repository.UserRepository;
import com.example.chatify.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final UserRepository userRepo;

    // ✅ Manual constructor for dependency injection
    public ChatRestController(ChatService chatService, UserRepository userRepo) {
        this.chatService = chatService;
        this.userRepo = userRepo;
    }

    @PostMapping("/direct")
    public Message sendDirect(@RequestParam Long from, @RequestParam Long to, @RequestParam String content) {
        return chatService.sendDirect(from, to, content);
    }

    @PostMapping("/group")
    public Message sendToGroup(@RequestParam Long from, @RequestParam Long groupId, @RequestParam String content) {
        return chatService.sendToGroup(from, groupId, content);
    }

    @GetMapping("/direct-thread")
    public List<Message> directThread(@RequestParam Long u1, @RequestParam Long u2) {
        return chatService.directThread(u1, u2);
    }

    @GetMapping("/group-thread")
    public List<Message> groupThread(@RequestParam Long groupId) {
        return chatService.groupThread(groupId);
    }
}
