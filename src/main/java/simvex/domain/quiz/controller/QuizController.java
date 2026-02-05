package simvex.domain.quiz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import simvex.domain.quiz.dto.*;
import simvex.domain.quiz.service.QuizService;
import simvex.global.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    // 퀴즈 랜덤 조회: /api/quizzes/random?modelId=1
    @GetMapping("/random")
    public ApiResponse<QuizRes> getRandomQuiz(@RequestParam Long modelId) {
        QuizRes data = quizService.getRandomQuiz(modelId);
        return ApiResponse.onSuccess(data);
    }

    // 정답 채점: /api/quizzes/{quizId}/check
    @PostMapping("/{quizId}/check")
    public ApiResponse<QuizResultRes> checkAnswer(
            @PathVariable Long quizId,
            @RequestBody QuizAnswerReq req
            ) {
        QuizResultRes data = quizService.checkAnswer(quizId, req);
        return ApiResponse.onSuccess(data);
    }

    @PostMapping("/create")
    public ApiResponse<QuizCreateRes> createQuiz(@RequestBody QuizCreateReq req) {
        QuizCreateRes data = quizService.createQuiz(req);
        return ApiResponse.onSuccess(data);
    }
}
