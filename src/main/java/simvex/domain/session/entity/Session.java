package simvex.domain.session.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.user.entity.UserEntity;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(columnDefinition = "jsonb")
    private String viewData;
}
