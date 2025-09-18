package com.example.chatify.controller;

import com.example.chatify.model.Message;
import com.example.chatify.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ✅ Send a message
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = payload.get("content").toString();

        Message message = messageService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.ok(message);
    }

    // ✅ Get conversation between two users
    @GetMapping("/{userId}/{friendId}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        return ResponseEntity.ok(messageService.getConversation(userId, friendId));
    }
}
