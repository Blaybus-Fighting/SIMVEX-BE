package simvex.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelObject model;

    private String question;

    private String answer;
}
