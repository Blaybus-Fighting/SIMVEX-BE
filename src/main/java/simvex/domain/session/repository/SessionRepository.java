package simvex.domain.session.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.session.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByIdAndUserId(Long id, Long userId);
}
