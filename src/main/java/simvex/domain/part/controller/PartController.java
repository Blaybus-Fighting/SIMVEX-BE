package simvex.domain.part.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import simvex.domain.part.dto.PartResponse;
import simvex.domain.part.service.PartService;
import simvex.global.dto.ApiResponse;

@Tag(name = "Part API", description = "부품 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/parts")
public class PartController {
    private final PartService partService;

    @Operation(summary = "부품 조회", description = "모델 관련 부품 조회")
    @GetMapping
    public ApiResponse<List<PartResponse>> getParts(@RequestParam(required = false) Long modelId) {
        List<PartResponse> partResponseList;
        if (modelId != null) {
            partResponseList = partService.getPartsByModelId(modelId);
        } else {
            partResponseList = partService.getAllParts();
        }
        return ApiResponse.onSuccess(partResponseList);
    }

    @Operation(summary = "부품 상세 조회", description = "부품 상세 조회")
    @GetMapping("/{partId}")
    public ApiResponse<PartResponse> getPart(
            @PathVariable Long partId
    ) {
        PartResponse partResponse = partService.getPart(partId);
        return ApiResponse.onSuccess(partResponse);
    }
}
