package simvex.domain.chat.dto;

import simvex.domain.chat.entity.ChatMessage;
import simvex.domain.chat.entity.ChatRole;

import java.time.LocalDateTime;

public record ChatMessageDto(
        ChatRole chatRole,
        String message,
        LocalDateTime timestamp
) {
    public static ChatMessageDto create(ChatRole role, String message) {
        return new ChatMessageDto(role, message, LocalDateTime.now());
    }

    public static ChatMessageDto trans(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage.getRole(), chatMessage.getMessage(), chatMessage.getCreatedAt());
    }
}
