package simvex.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
