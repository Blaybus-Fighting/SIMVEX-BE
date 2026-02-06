package simvex.domain.part.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.part.dto.PartResponse;
import simvex.domain.part.entity.Part;
import simvex.domain.part.repository.PartRepository;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;
import simvex.global.infra.s3.S3Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartService {
    private final PartRepository partRepository;
    private final ModelObjectRepository modelObjectRepository;
    private final S3Service s3Service;

    public List<PartResponse> getAllParts(){
        return partRepository.findAll().stream()
                .map(part -> {
                    String presignedUrl = s3Service.getPresignedUrl(part.getModelUrl());

                    return PartResponse.from(part, presignedUrl);
                })
                .toList();
    }

    public List<PartResponse> getPartsByModelId(Long modelId) {
        if (!modelObjectRepository.existsById(modelId)) {
            throw new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND);
        }

        return partRepository.findAllByModelId(modelId).stream()
                .map(part -> {
                    String presignedUrl = s3Service.getPresignedUrl(part.getModelUrl());

                    return PartResponse.from(part, presignedUrl);
                })
                .toList();
    }

    public PartResponse getPart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PART_NOT_FOUND));
        String presignedUrl = s3Service.getPresignedUrl(part.getModelUrl());
        return PartResponse.from(part, presignedUrl);
    }
}
