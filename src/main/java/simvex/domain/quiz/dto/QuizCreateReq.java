package simvex.domain.quiz.dto;


public record QuizCreateReq(
        Long modelId,
        String question,
        String answer
) {
}
