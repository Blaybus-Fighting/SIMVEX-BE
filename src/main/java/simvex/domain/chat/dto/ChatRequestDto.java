package simvex.domain.chat.dto;

import javax.validation.constraints.NotNull;

public record ChatRequestDto (
    @NotNull String question,
    Long sessionId
) { }
