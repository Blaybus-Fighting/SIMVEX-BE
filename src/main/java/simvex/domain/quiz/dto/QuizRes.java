package simvex.domain.quiz.dto;

// 문제 조회 DTO
public record QuizRes(
        Long quizId,
        String question
) {
}
