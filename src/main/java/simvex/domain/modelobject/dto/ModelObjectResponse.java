package simvex.domain.modelobject.dto;

import lombok.Builder;
import lombok.Getter;
import simvex.domain.modelobject.entity.ModelObject;

@Getter
@Builder
public class ModelObjectResponse {
    private Long id;
    private String name;
    private String description;
    private String thumbnailUrl;
    private String usage;
    private String mainTheory;

    public static ModelObjectResponse from(ModelObject modelObject, String presignedUrl) {
        return ModelObjectResponse.builder()
                .id(modelObject.getId())
                .name(modelObject.getName())
                .description(modelObject.getDescription())
                .thumbnailUrl(presignedUrl)
                .usage(modelObject.getUsage())
                .mainTheory(modelObject.getMainTheory())
                .build();
    }
}
