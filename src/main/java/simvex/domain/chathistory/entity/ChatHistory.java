package simvex.domain.chathistory.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.session.entity.Session;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;


    @Column(columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    private ChatRole role;
}
