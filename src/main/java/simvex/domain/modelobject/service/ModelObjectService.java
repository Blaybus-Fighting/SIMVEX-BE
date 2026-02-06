package simvex.domain.modelobject.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simvex.domain.modelobject.dto.ModelObjectResponse;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.domain.user.entity.User;
import simvex.domain.user.repository.UserRepository;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
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

    public List<ModelObjectResponse> getAllModelObjects() {
        return modelObjectRepository.findAll().stream()
                .map(model -> {
                    String presignedUrl = s3Service.getPresignedUrl(model.getThumbnailUrl());

                    return ModelObjectResponse.from(model, presignedUrl);
                })
                .toList();
    }

    @Transactional
    public ModelObjectResponse getModelObjectDetail(Long modelObjectId) {
        PrincipalOAuth2User principal = (PrincipalOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getId();

        ModelObject modelObject = modelObjectRepository.findById(modelObjectId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));

        if (sessionRepository.findByUserIdAndModelObjectId(userId, modelObjectId).isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));


            Session newSession = Session.create(user, modelObject);
            sessionRepository.save(newSession);
        }

        String presignedUrl = s3Service.getPresignedUrl(modelObject.getThumbnailUrl());

        return ModelObjectResponse.from(modelObject, presignedUrl);
    }
}
