package simvex.domain.session.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simvex.domain.modelobject.repository.ModelObjectRepository;
import simvex.domain.session.dto.SessionReq;
import simvex.domain.session.dto.SessionRes;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ModelObjectRepository modelRepository;

    // 세션 조회
    public SessionRes getSession(Long userId, Long modelId) {
        Session session = sessionRepository.findByUserIdAndModelObjectId(userId, modelId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return new SessionRes(session.getId(), session.getModelObject().getId(), session.getViewData());
    }


    @Transactional
    public SessionRes updateSession(SessionReq sessionReq) {
        Session session = sessionRepository.findById(sessionReq.sessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        session.updateViewData(sessionReq.viewData());
        return new SessionRes(
                session.getId(),
                session.getModelObject().getId(),
                session.getViewData()
        );
    }
}
