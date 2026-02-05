package simvex.domain.session.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.domain.session.dto.SessionReq;
import simvex.domain.session.dto.SessionRes;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.domain.user.entity.User;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ModelObjectRepository modelRepository;

    // 세션 조회
    public SessionRes getSession(User user, Long modelId) {
        Session session = sessionRepository.findByUserIdAndModelId(user.getId(), modelId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return new SessionRes(session.getModel().getId(), session.getViewData());
    }

    /**
     * (userId, modelId) 조합으로 세션을 조회해서
     * 있으면 viewData 교체 후 저장
     * 없으면 새로 생성 후 저장
     */
    @Transactional
    public SessionRes saveOrUpdate(User user, Long modelId, SessionReq req) {
        ModelObject model = modelRepository.findById(modelId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODEL_OBJECT_NOT_FOUND));

        Session session = sessionRepository.findByUserIdAndModelId(user.getId(), modelId)
                .map(existing -> Session.update(existing, req.viewData()))
                .orElse(Session.create(user, model, req.viewData()));

        Session saved = sessionRepository.save(session);

        return new SessionRes(saved.getModel().getId(), saved.getViewData());
    }
}
