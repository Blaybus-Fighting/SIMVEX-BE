package simvex.domain.session.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.domain.user.entity.User;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "model_id"})
        }
)
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelObject model;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String viewData;

    /* ====== 팩토리 메서드 ====== */

    /** 신규 세션 생성 */
    public static Session create(User user, ModelObject model, String viewData) {
        return Session.builder()
                .user(user)
                .model(model)
                .viewData(viewData)
                .build();
    }

    /** 기존 세션 갱신 (setter 없이) */
    public static Session update(Session existing, String viewData) {
        return Session.builder()
                .id(existing.getId())
                .user(existing.getUser())
                .model(existing.getModel())
                .viewData(viewData)
                .build();
    }
}
