package simvex.domain.chat.dto;

public record SseMessageDto(
        String type,
        String message,
        Integer sequence
) {
}
