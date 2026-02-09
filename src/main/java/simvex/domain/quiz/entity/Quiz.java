package simvex.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import simvex.domain.modelobject.entity.ModelObject;
import simvex.global.common.BaseEntity;

import java.util.List;

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

    @Column(nullable = false)
    private String question;

    @ElementCollection
    @CollectionTable(name = "quiz_options", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "option_text")
    private List<String> options;

    @Column(nullable = false)
    private int answer;

    @Column(nullable = false)
    private String explanation;

    // 퀴즈 생성 메서드
    public static Quiz create(ModelObject model, String question, List<String> options, int answer, String explanation) {
        return Quiz.builder()
                .model(model)
                .question(question)
                .options(options)
                .answer(answer)
                .explanation(explanation)
                .build();
    }
}
