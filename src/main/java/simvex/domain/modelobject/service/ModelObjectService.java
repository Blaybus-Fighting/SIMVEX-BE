package simvex.domain.modelobject.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelObjectService {

    private final ModelObjectRepository modelObjectRepository;

    public List<ModelObjectResponse> getAllModelObjects() {
        return modelObjectRepository.findAll().stream()
                .map(ModelObjectResponse::from)
                .toList();
    }

    public ModelObjectResponse getModelObjectDetail(Long modelObjectId) {
        ModelObject modelObject = modelObjectRepository.findById(modelObjectId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));
        return ModelObjectResponse.from(modelObject);
    }
}
