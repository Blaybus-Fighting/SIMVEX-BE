package simvex.domain.modelobject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import simvex.domain.modelobject.entity.ModelObject;

public interface ModelObjectRepository extends JpaRepository<ModelObject, Long> {
    List<ModelObjectSummary> findAllProjectedBy();
}
