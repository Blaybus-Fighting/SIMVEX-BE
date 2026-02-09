package simvex.domain.quiz.dto;

import java.util.List;

// 문제 조회 DTO
public record QuizRes(
        Long modelId,
        List<QuizData> quizzes
) {
}
