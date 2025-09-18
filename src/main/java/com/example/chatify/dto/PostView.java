package com.example.chatify.dto;

import java.time.LocalDateTime;

public record PostView(Long id, String author, String text, LocalDateTime createdAt) {}
