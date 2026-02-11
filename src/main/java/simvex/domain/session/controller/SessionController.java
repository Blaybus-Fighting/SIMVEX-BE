package simvex.domain.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simvex.domain.session.dto.SessionReq;
import simvex.domain.session.dto.SessionRes;
import simvex.domain.session.service.SessionService;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.dto.ApiResponse;

@Tag(name = "Session API", description = "사용자 줌인/줌아웃, 모델 좌표 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/models/{modelId}/session")
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "세션 조회", description = "사용자가 모델 화면에 접속했을 때, 기존 세션 정보 조회")
    @GetMapping
    public ApiResponse<SessionRes> getSession(
            @AuthenticationPrincipal PrincipalOAuth2User user,
            @PathVariable Long modelId
            ) {
        return ApiResponse.onSuccess(
                sessionService.getSession(user.getId(), modelId)
        );
    }

    @Operation(summary = "세션 업데이트", description = "세션 생성 및 업데이트")
    @PutMapping
    public ApiResponse<SessionRes> updateSession(
            @AuthenticationPrincipal PrincipalOAuth2User user,
            @RequestBody SessionReq session
    ) {
        return ApiResponse.onSuccess(
                sessionService.updateSession(session)
        );
    }
}
