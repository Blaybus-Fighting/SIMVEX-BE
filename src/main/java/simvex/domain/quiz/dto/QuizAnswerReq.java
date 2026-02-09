package simvex.domain.quiz.dto;

// 정답 제출 DTO
public record QuizAnswerReq(
        Long quizId,
        int selectedAnswer
) {
}
