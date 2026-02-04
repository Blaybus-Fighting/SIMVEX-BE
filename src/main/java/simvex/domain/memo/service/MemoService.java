package simvex.domain.memo.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simvex.domain.memo.dto.MemoRequest;
import simvex.domain.memo.dto.MemoResponse;
import simvex.domain.memo.entity.Memo;
import simvex.domain.memo.repository.MemoRepository;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.domain.user.entity.User;
import simvex.domain.user.repository.UserRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createMemo(Long userId, MemoRequest.Create request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Session session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        Memo memo = Memo.create(user, session, request.content());

        return memoRepository.save(memo).getId();
    }

    public List<MemoResponse> getMemos(Long userId, Long sessionId) {
        // 본인의 세션인지 검증
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        return memoRepository.findAllBySessionIdAndUserId(sessionId, userId)
            .stream()
            .map(MemoResponse::from)
            .toList();
    }

    @Transactional
    public void updateMemo(Long userId, Long memoId, MemoRequest.Update request) {
        Memo memo = memoRepository.findById(memoId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        if (!memo.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

         memo.updateContent(request.content());
    }

    @Transactional
    public void deleteMemo(Long userId, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        if (!memo.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        memoRepository.delete(memo);
    }
}
