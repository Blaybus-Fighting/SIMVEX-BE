package simvex.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.domain.quiz.dto.*;
import simvex.domain.quiz.entity.Quiz;
import simvex.domain.quiz.repository.QuizRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModelObjectRepository modelObjectRepository;

    // 퀴즈 조회
    public QuizRes getRandomQuiz(Long modelId) {
        Quiz quiz = quizRepository.findRandomByModelId(modelId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        return new QuizRes(
                quiz.getId(),
                quiz.getQuestion()
        );
    }

    // 퀴즈 정답 채점
    public QuizResultRes checkAnswer(Long quizId, QuizAnswerReq req) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        boolean correct = quiz.getAnswer().trim().equalsIgnoreCase(req.answer().trim());
        return new QuizResultRes(correct);
    }

    // 퀴즈 생성 - 관리자용
    public QuizCreateRes createQuiz(QuizCreateReq req) {
        ModelObject model = modelObjectRepository.findById(req.modelId())
                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));

        Quiz quiz = Quiz.create(
                model,
                req.question(),
                req.answer()
        );

        quizRepository.save(quiz);

        return new QuizCreateRes(quiz.getId());
    }


}
