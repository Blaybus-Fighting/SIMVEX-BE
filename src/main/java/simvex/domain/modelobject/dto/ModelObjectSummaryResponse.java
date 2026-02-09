package simvex.domain.modelobject.dto;

import lombok.Builder;
import lombok.Getter;
import simvex.domain.modelobject.repository.ModelObjectSummary;

@Getter
@Builder
public class ModelObjectSummaryResponse {
    private Long id;
    private String name;
    private String thumbnailUrl;

    public static ModelObjectSummaryResponse from(ModelObjectSummary summary, String presignedUrl) {
        return ModelObjectSummaryResponse.builder()
                .id(summary.getId())
                .name(summary.getName())
                .thumbnailUrl(presignedUrl)
                .build();
    }
}
