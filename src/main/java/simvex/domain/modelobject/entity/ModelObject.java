package simvex.domain.modelobject.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.session.entity.Session;
import simvex.domain.user.entity.UserEntity;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@Table(name = "model_objects")
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelObject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    private String name;

    private String description;

    private String thumbnailUrl;

    private String systemPrompt;
}
