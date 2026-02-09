package simvex.domain.modelobject.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.modelobject.dto.ModelObjectSummaryResponse;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.domain.session.service.SessionService;
import simvex.domain.user.entity.User;
import simvex.domain.user.repository.UserRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;
import simvex.global.infra.s3.S3Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelObjectService {

    private final ModelObjectRepository modelObjectRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final SessionService sessionService;

    public List<ModelObjectSummaryResponse> getAllModelObjects(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return modelObjectRepository.findAllProjectedBy().stream()
                .map(summary -> {
                    String presignedUrl = s3Service.getPresignedUrl(summary.getThumbnailUrl());

                    return ModelObjectSummaryResponse.from(summary, presignedUrl);
                })
                .toList();
    }

    @Transactional
    public ModelObjectResponse getModelObjectDetail(Long modelObjectId, Long userId) {
        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // ModelObject 조회
        ModelObject modelObject = modelObjectRepository.findById(modelObjectId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));

        // Session 조회 및 없을 시 생성 (변수에 저장하여 재사용)
        Session session = sessionRepository.findByUserIdAndModelObjectId(userId, modelObjectId)
                .orElseGet(() -> {
                    Session newSession = Session.create(user, modelObject);
                    return sessionRepository.save(newSession);
                });

        String presignedUrl = s3Service.getPresignedUrl(modelObject.getThumbnailUrl());

        return ModelObjectResponse.from(modelObject, presignedUrl, session.getViewData());
    }
}
