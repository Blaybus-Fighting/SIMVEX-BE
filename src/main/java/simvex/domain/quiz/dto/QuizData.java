package simvex.domain.quiz.dto;

import simvex.domain.quiz.entity.Quiz;

import java.util.List;

public record QuizData(
        Long quizId,
        String question,
        List<String> options,
        int answer,
        String explanation
) {
    public static QuizData from(Quiz quiz) {
        return new QuizData(
                quiz.getId(),
                quiz.getQuestion(),
                quiz.getOptions(),
                quiz.getAnswer(),
                quiz.getExplanation()
        );
    }
}