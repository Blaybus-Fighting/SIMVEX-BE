package simvex.domain.quiz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import simvex.domain.quiz.dto.*;
import simvex.domain.quiz.service.QuizService;
import simvex.global.dto.ApiResponse;

import java.util.List;

@Tag(name = "Quiz API", description = "퀴즈 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    // 퀴즈 랜덤 조회: /api/quizzes/random?modelId=1
    @Operation(summary = "퀴즈 조회", description = "모델 관련 퀴즈 조회")
    @GetMapping
    public ApiResponse<QuizRes> getQuizzesByModel(@RequestParam Long modelId) {
        QuizRes data = quizService.getQuizzesByModel(modelId);
        return ApiResponse.onSuccess(data);
    }

//    // 정답 채점: /api/quizzes/{quizId}/check
//    @Operation(summary = "퀴즈 정답 채점", description = "사용자가 제출한 퀴즈 정답 채점")
//    @PostMapping("/{modelId}/check")
//    public ApiResponse<QuizResultRes> checkAnswer(
//            @PathVariable Long modelId,
//            @RequestBody List<QuizAnswerReq> req
//            ) {
//        QuizResultRes data = quizService.checkAnswer(modelId, req);
//        return ApiResponse.onSuccess(data);
//    }

//    @Operation(summary = "퀴즈 생성", description = "퀴즈 생성 (관리자용)")
//    @PostMapping("/create")
//    public ApiResponse<QuizCreateRes> createQuiz(@RequestBody QuizCreateReq req) {
//        QuizCreateRes data = quizService.createQuiz(req);
//        return ApiResponse.onSuccess(data);
//    }
}
