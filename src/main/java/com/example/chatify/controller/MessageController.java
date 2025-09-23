package com.example.chatify.controller;

import com.example.chatify.dto.SendDirectMessageRequest;
import com.example.chatify.dto.SendGroupMessageRequest;
import com.example.chatify.model.Message;
import com.example.chatify.model.User;
import com.example.chatify.service.MessageService;
import com.example.chatify.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate template; // ✅ add this

    public MessageController(MessageService messageService,
                             UserService userService,
                             SimpMessagingTemplate template) {
        this.messageService = messageService;
        this.userService = userService;
        this.template = template;
    }

    // ================= Direct messages =================

    @PostMapping("/direct/send")
    public ResponseEntity<Message> sendDirectMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SendDirectMessageRequest request) {

        User sender = userService.findByUsername(userDetails.getUsername());
        Message saved = messageService.sendDirectMessage(
                sender.getId(),
                request.getRecipientId(),
                request.getContent()
        );

        // ✅ Broadcast over WS so both sides get it in realtime
        template.convertAndSendToUser(
                saved.getRecipient().getUsername(),
                "/queue/messages",
                saved
        );
        // echo to sender too (so they also get the WS copy)
        template.convertAndSendToUser(
                saved.getSender().getUsername(),
                "/queue/messages",
                saved
        );

        return ResponseEntity.ok(saved);
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

        User sender = userService.findByUsername(userDetails.getUsername());
        Message saved = messageService.sendGroupMessage(
                sender.getId(),
                groupId,
                request.getContent()
        );

        // ✅ Broadcast to subscribers of the group topic
        template.convertAndSend("/topic/group/" + groupId, saved);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Message>> getGroupMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(messageService.getGroupMessages(groupId));
    }
}
