package simvex.domain.memo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.memo.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllBySessionIdAndUserId(Long sessionId, Long userId);
}
