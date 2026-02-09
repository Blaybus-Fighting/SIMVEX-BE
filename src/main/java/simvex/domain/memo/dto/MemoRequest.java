package simvex.domain.memo.dto;

public class MemoRequest {

    public record Create(
        Long sessionId,

        String content
    ) {}

    public record Update(
        String content
    ) {}
}
