package simvex.domain.session.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_object_id")
    private ModelObject modelObject;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String viewData;

    /* ====== 팩토리 메서드 ====== */

    /** 세션 최초 생성 (viewData 없음) */
    public static Session create(User user, ModelObject modelObject) {
        return Session.builder()
                .user(user)
                .modelObject(modelObject)
                .build();
    }

    /** viewData 업데이트 **/
    public static Session update(Session existing, String viewData) {
        return Session.builder()
                .id(existing.getId())
                .user(existing.getUser())
                .modelObject(existing.getModelObject())
                .viewData(viewData)
                .build();
    }
}
