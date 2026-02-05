package simvex.domain.modelobject.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.modelobject.service.ModelObjectService;
import simvex.global.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/model-objects")
public class ModelObjectController {

    private final ModelObjectService modelObjectService;

    @GetMapping
    public ApiResponse<List<ModelObjectResponse>> getModelObjects() {
        List<ModelObjectResponse> responses = modelObjectService.getAllModelObjects();
        return ApiResponse.onSuccess(responses);
    }

    @GetMapping("/{modelObjectId}")
    public ApiResponse<ModelObjectResponse> getModelObject(
            @PathVariable Long modelObjectId
    ) {
        ModelObjectResponse response = modelObjectService.getModelObjectDetail(modelObjectId);
        return ApiResponse.onSuccess(response);
    }
}
