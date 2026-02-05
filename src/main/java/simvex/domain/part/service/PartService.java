package simvex.domain.part.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simvex.domain.part.dto.PartResponse;
import simvex.domain.part.entity.Part;
import simvex.domain.part.repository.PartRepository;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class PartService {
    private final PartRepository partRepository;
    private final ModelObjectRepository modelObjectRepository;

    public List<PartResponse> getAllParts(){
        return partRepository.findAll().stream()
                .map(PartResponse::from)
                .toList();
    }

    public List<PartResponse> getPartsByModelId(Long modelId) {
        if (!modelObjectRepository.existsById(modelId)) {
            throw new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND);
        }

        return partRepository.findAllByModelId(modelId).stream()
                .map(PartResponse::from)
                .toList();
    }

    public PartResponse getPart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PART_NOT_FOUND));
        return PartResponse.from(part);
    }
}
