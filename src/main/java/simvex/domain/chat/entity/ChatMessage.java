package simvex.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.session.entity.Session;
import simvex.global.common.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

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

    private ChatMessage(Session session, String message, ChatRole role) {
        this.session = session;
        this.message = message;
        this.role = role;
    }

    public static ChatMessage create(Session session, String message, ChatRole role) {
        return new ChatMessage(session, message, role);
    }
}
