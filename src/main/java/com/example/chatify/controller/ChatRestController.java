package com.example.chatify.controller;

import com.example.chatify.model.Message;
import com.example.chatify.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    // ✅ Send direct message via REST
    @PostMapping("/direct")
    public ResponseEntity<Message> sendDirect(@RequestBody Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long recipientId = Long.valueOf(payload.get("recipientId").toString());
        String content = payload.get("content").toString();

        Message msg = chatService.sendDirect(senderId, recipientId, content);
        return ResponseEntity.ok(msg);
    }

    // ✅ Get direct conversation between two users
    @GetMapping("/direct/{userId}/{friendId}")
    public ResponseEntity<List<Message>> getDirectThread(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        return ResponseEntity.ok(chatService.directThread(userId, friendId));
    }

    // ✅ Send group message
    @PostMapping("/group")
    public ResponseEntity<Message> sendGroup(@RequestBody Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long groupId = Long.valueOf(payload.get("groupId").toString());
        String content = payload.get("content").toString();

        Message msg = chatService.sendToGroup(senderId, groupId, content);
        return ResponseEntity.ok(msg);
    }

    // ✅ Get group conversation
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Message>> getGroupThread(@PathVariable Long groupId) {
        return ResponseEntity.ok(chatService.groupThread(groupId));
    }
}
