package simvex.domain.quiz.dto;

import java.util.List;

// 채점 결과 DTO
public record QuizResultRes(
        int totalCount,    // 전체 문제 수
        int correctCount,  // 맞힌 문제 수
        List<Boolean> details // 각 문제별 정답 여부
) {}