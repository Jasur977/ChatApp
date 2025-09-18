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

    public ChatWsController(ChatService chatService, SimpMessagingTemplate template) {
        this.chatService = chatService;
        this.template = template;
    }

    // ✅ Direct (1-to-1) messages
    @MessageMapping("/direct")
    public void processDirect(@Payload Message message) {
        Message saved = chatService.sendDirect(
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getContent()
        );

        // Send to recipient’s personal queue
        template.convertAndSendToUser(
                message.getRecipient().getUsername(),
                "/queue/messages",
                saved
        );
    }

    // ✅ Group messages
    @MessageMapping("/group")
    public void processGroup(@Payload Message message) {
        Message saved = chatService.sendToGroup(
                message.getSender().getId(),
                message.getGroupChat().getId(),
                message.getContent()
        );

        // Broadcast to group topic
        template.convertAndSend(
                "/topic/group/" + message.getGroupChat().getId(),
                saved
        );
    }
}
