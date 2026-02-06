package simvex.domain.part.dto;

import lombok.Builder;
import lombok.Getter;
import simvex.domain.part.entity.Part;


@Builder
@Getter
public class PartResponse {
    private Long id;
    private String name;
    private String material;
    private String roleDescription;
    private String modelUrl;
    private String localCoordinates;

    public static PartResponse from(Part part, String presignedUrl){
        return PartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .material(part.getMaterial())
                .roleDescription(part.getRoleDescription())
                .modelUrl(presignedUrl)
                .localCoordinates(part.getLocalCoordinates())
                .build();
    }
}
