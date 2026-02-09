package simvex.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByProviderUserId(String providerUserId);
}
