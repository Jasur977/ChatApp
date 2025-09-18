package com.example.chatify.dto;

import java.time.LocalDateTime;

public record MessageView(
        Long id,
        Long senderId,
        Long recipientId,
        Long groupId,
        String content,
        LocalDateTime sentAt
) {}
