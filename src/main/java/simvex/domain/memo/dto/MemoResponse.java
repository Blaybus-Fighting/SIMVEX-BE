package simvex.domain.memo.dto;

import java.time.LocalDateTime;
import simvex.domain.memo.entity.Memo;

public record MemoResponse(
    Long id,
    Long sessionId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static MemoResponse from(Memo memo) {
        return new MemoResponse(
            memo.getId(),
            memo.getSession().getId(),
            memo.getContent(),
            memo.getCreatedAt(),
            memo.getUpdatedAt()
        );
    }
}
