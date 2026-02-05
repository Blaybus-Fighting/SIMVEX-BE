package simvex.domain.part.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.part.entity.Part;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findAllByModelId(Long modelId);
}

