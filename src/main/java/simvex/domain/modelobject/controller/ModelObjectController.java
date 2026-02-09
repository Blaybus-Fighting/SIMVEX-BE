package simvex.domain.modelobject.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.modelobject.dto.ModelObjectSummaryResponse;
import simvex.domain.modelobject.service.ModelObjectService;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.dto.ApiResponse;

@Tag(name = "Model Object API", description = "모델 객체 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/model-objects")
public class ModelObjectController {

    private final ModelObjectService modelObjectService;

    @Operation(summary = "모델 객체 리스트 조회")
    @GetMapping
    public ApiResponse<List<ModelObjectSummaryResponse>> getModelObjects(
            @AuthenticationPrincipal PrincipalOAuth2User principal
    ) {
        Long userId = principal.getId();

        List<ModelObjectSummaryResponse> responses = modelObjectService.getAllModelObjects(userId);
        return ApiResponse.onSuccess(responses);
    }

    @Operation(summary = "모델 객체 상세 조회")
    @GetMapping("/{modelObjectId}")
    public ApiResponse<ModelObjectResponse> getModelObject(
            @PathVariable Long modelObjectId,
            @AuthenticationPrincipal PrincipalOAuth2User principal
    ) {
        Long userId = principal.getId();

        ModelObjectResponse response = modelObjectService.getModelObjectDetail(modelObjectId, userId);
        return ApiResponse.onSuccess(response);
    }
}
