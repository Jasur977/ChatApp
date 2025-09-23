package com.example.chatify.controller;

import com.example.chatify.dto.SendDirectMessageRequest;
import com.example.chatify.dto.SendGroupMessageRequest;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.service.MessageService;
import com.example.chatify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    // ================= Direct messages =================

    @PostMapping("/direct/send")
    public ResponseEntity<Message> sendDirectMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SendDirectMessageRequest request) {

        // ✅ Find sender using authenticated principal
        User sender = userService.findByUsername(userDetails.getUsername());

        Message message = messageService.sendDirectMessage(
                sender.getId(),
                request.getRecipientId(),
                request.getContent()
        );
        return ResponseEntity.ok(message);
    }

    @GetMapping("/direct/{userId}/{friendId}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long friendId) {

        return ResponseEntity.ok(messageService.getConversation(userId, friendId));
    }

    // ================= Group messages =================

    @PostMapping("/group/{groupId}/send")
    public ResponseEntity<Message> sendGroupMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId,
            @RequestBody SendGroupMessageRequest request) {

        // ✅ Find sender from JWT principal
        User sender = userService.findByUsername(userDetails.getUsername());

        Message message = messageService.sendGroupMessage(
                sender.getId(),
                groupId,
                request.getContent()
        );
        return ResponseEntity.ok(message);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Message>> getGroupMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(messageService.getGroupMessages(groupId));
    }
}
