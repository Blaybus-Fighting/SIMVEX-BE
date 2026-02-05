package simvex.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import simvex.domain.quiz.entity.Quiz;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query(value = """
            SELECT *
            FROM quiz
            WHERE model_id = :modelId
            ORDER BY RANDOM()
            LIMIT 1
            """, nativeQuery = true)
    Optional<Quiz> findRandomByModelId(@Param("modelId") Long modelId);
}
