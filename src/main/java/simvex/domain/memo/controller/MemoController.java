package simvex.domain.memo.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import simvex.domain.memo.dto.MemoRequest;
import simvex.domain.memo.dto.MemoResponse;
import simvex.domain.memo.service.MemoService;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.dto.ApiResponse;

@Tag(name = "Memo API", description = "메모 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;

    @Operation(summary = "메모 생성")
    @PostMapping
    public ApiResponse<Long> createMemo(
        @AuthenticationPrincipal PrincipalOAuth2User principal,
        @RequestBody  MemoRequest.Create request
    ) {
        Long memoId = memoService.createMemo(principal.getId(), request);
        return ApiResponse.onSuccess(memoId);
    }

    @Operation(summary = "메모 조회")
    @GetMapping
    public ApiResponse<List<MemoResponse>> getMemos(
        @AuthenticationPrincipal PrincipalOAuth2User principal,
        @RequestParam Long sessionId
    ) {
        List<MemoResponse> responses = memoService.getMemos(principal.getId(), sessionId);
        return ApiResponse.onSuccess(responses);
    }

    @Operation(summary = "메모 수정")
    @PatchMapping("/{memoId}")
    public ApiResponse<Void> updateMemo(
        @AuthenticationPrincipal PrincipalOAuth2User principal,
        @PathVariable Long memoId,
        @RequestBody MemoRequest.Update request
    ) {
        memoService.updateMemo(principal.getId(), memoId, request);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "메모 삭제")
    @DeleteMapping("/{memoId}")
    public ApiResponse<Void> deleteMemo(
        @AuthenticationPrincipal PrincipalOAuth2User principal,
        @PathVariable Long memoId
    ) {
        memoService.deleteMemo(principal.getId(), memoId);
        return ApiResponse.onSuccess();
    }
}
