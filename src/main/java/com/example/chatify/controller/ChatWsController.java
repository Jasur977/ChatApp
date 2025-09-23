package com.example.chatify.controller;

import com.example.chatify.dto.SendDirectMessageRequest;
import com.example.chatify.dto.SendGroupMessageRequest;
import com.example.chatify.model.Message;
import com.example.chatify.repository.UserRepository;
import com.example.chatify.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    private final UserRepository userRepo;

    public ChatWsController(ChatService chatService,
                            SimpMessagingTemplate template,
                            UserRepository userRepo) {
        this.chatService = chatService;
        this.template = template;
        this.userRepo = userRepo;
    }

    // ✅ Direct (1-to-1) messages
    @MessageMapping("/direct")
    public void processDirect(@Payload SendDirectMessageRequest dto, Principal principal) {
        String senderUsername = principal.getName();
        System.out.println("📩 Direct message received from: " + senderUsername);
        System.out.println("Recipient ID in payload: " + dto.getRecipientId());

        // 🔹 Resolve sender ID from principal
        Long senderId = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"))
                .getId();

        // Save message in DB
        Message saved = chatService.sendDirect(
                senderId,
                dto.getRecipientId(),
                dto.getContent()
        );

        // Deliver to recipient
        template.convertAndSendToUser(
                saved.getRecipient().getUsername(),
                "/queue/messages",
                saved
        );

        // Echo back to sender
        template.convertAndSendToUser(
                saved.getSender().getUsername(),
                "/queue/messages",
                saved
        );

        System.out.println("📤 Sent message to user: " + saved.getRecipient().getUsername());
    }

    // ✅ Group messages
    @MessageMapping("/group")
    public void processGroup(@Payload SendGroupMessageRequest dto, Principal principal) {
        String senderUsername = principal.getName();
        System.out.println("👥 Group message received from: " + senderUsername);
        System.out.println("Group ID in payload: " + dto.getGroupId());

        // 🔹 Resolve sender ID from principal
        Long senderId = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"))
                .getId();

        // Save message in DB
        Message saved = chatService.sendToGroup(
                senderId,
                dto.getGroupId(),
                dto.getContent()
        );

        // Broadcast to group subscribers
        template.convertAndSend(
                "/topic/group/" + saved.getGroupChat().getId(),
                saved
        );

        System.out.println("📢 Broadcasted message to group: " + saved.getGroupChat().getId());
    }
}
