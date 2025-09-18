package com.example.chatify.controller;

import com.example.chatify.model.Message;
import com.example.chatify.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    // ✅ Manual constructor for dependency injection
    public ChatWsController(ChatService chatService, SimpMessagingTemplate template) {
        this.chatService = chatService;
        this.template = template;
    }

    // Handle direct messages over WebSocket
    @MessageMapping("/direct")
    public void processDirect(@Payload Message message) {
        Message saved = chatService.sendDirect(
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getContent()
        );
        template.convertAndSendToUser(
                message.getRecipient().getUsername(),
                "/queue/messages",
                saved
        );
    }

    // Handle group messages over WebSocket
    @MessageMapping("/group")
    public void processGroup(@Payload Message message) {
        Message saved = chatService.sendToGroup(
                message.getSender().getId(),
                message.getGroupChat().getId(),
                message.getContent()
        );
        template.convertAndSend(
                "/topic/group/" + message.getGroupChat().getId(),
                saved
        );
    }
}
