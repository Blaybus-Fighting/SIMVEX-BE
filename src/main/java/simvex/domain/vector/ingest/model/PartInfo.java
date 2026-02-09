package simvex.domain.vector.ingest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PartInfo(
        @JsonProperty("part_name") String partName,
        @JsonProperty("description") String description,
        @JsonProperty("material") String material,
        @JsonProperty("function") String function,
        @JsonProperty("impact") String impact
) {
}
