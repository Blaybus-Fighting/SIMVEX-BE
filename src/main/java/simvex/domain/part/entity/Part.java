package simvex.domain.part.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.modelobject.entity.ModelObject;

@Getter
@Entity
@Table(name = "parts")
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelObject model;

    private String name;

    private String material;

    private String roleDescription;

    private String modelUrl;

    @Column(columnDefinition = "jsonb")
    private String localCoordinates;
}
