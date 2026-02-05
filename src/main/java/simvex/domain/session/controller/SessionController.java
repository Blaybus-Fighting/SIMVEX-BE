package simvex.domain.session.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import simvex.domain.session.dto.SessionReq;
import simvex.domain.session.dto.SessionRes;
import simvex.domain.session.service.SessionService;
import simvex.domain.user.entity.User;
import simvex.global.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/models/{modelId}/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ApiResponse<SessionRes> getSession(
            @AuthenticationPrincipal User user,
            @PathVariable Long modelId
            ) {
        return ApiResponse.onSuccess(
                sessionService.getSession(user, modelId)
        );
    }

    @PutMapping
    public ApiResponse<SessionRes> saveOrUpdate(
            @AuthenticationPrincipal User user,
            @PathVariable Long modelId,
            @RequestBody SessionReq req
            ) {
        return ApiResponse.onSuccess(
                sessionService.saveOrUpdate(user, modelId, req)
        );
    }
}
