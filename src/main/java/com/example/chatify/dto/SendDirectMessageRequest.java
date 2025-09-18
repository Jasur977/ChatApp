package com.example.chatify.dto;

public record SendDirectMessageRequest(Long toUserId, String content) {}
