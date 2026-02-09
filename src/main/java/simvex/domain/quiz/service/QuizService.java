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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModelObjectRepository modelObjectRepository;

    // 퀴즈 조회
    public QuizRes getQuizzesByModel(Long modelId) {
        List<Quiz> quizList = quizRepository.findAllByModelId(modelId);

        List<QuizData> quizDataList = quizList.stream()
                .map(quiz -> new QuizData(
                        quiz.getId(),
                        quiz.getQuestion(),
                        quiz.getOptions(),
                        quiz.getAnswer(),
                        quiz.getExplanation()
                ))
                .toList();

        return new QuizRes(modelId, quizDataList);
    }

//    // 퀴즈 정답 채점
//    public QuizResultRes checkAnswer(Long modelId, List<QuizAnswerReq> answerReqs) {
//        Map<Long, Integer> answerMap = quizRepository.findAllByModelId(modelId).stream()
//                .collect(Collectors.toMap(Quiz::getId, Quiz::getAnswer));
//
//        int correctCount = 0;
//        List<Boolean> details = new ArrayList<>();
//
//        for (QuizAnswerReq req : answerReqs) {
//            Integer correctAnswer = answerMap.get(req.quizId());
//
//            boolean isCorrect = correctAnswer != null &&
//                    correctAnswer == req.selectedAnswer();
//
//            if (isCorrect) correctCount++;
//            details.add(isCorrect);
//        }
//
//        return new QuizResultRes(answerMap.size(), correctCount, details);
//    }

//    // 퀴즈 생성 - 관리자용
//    public QuizCreateRes createQuiz(QuizCreateReq req) {
//        ModelObject model = modelObjectRepository.findById(req.modelId())
//                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));
//
//        Quiz quiz = Quiz.create(
//                model,
//                req.question(),
//                req.answer()
//        );
//
//        quizRepository.save(quiz);
//
//        return new QuizCreateRes(quiz.getId());
//    }


}
