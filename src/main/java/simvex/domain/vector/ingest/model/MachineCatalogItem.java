package simvex.domain.vector.ingest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MachineCatalogItem(
        @JsonProperty("model_name") String modelName,
        @JsonProperty("machine_info") MachineInfo machineInfo,
        @JsonProperty("parts") List<PartInfo> parts
) {
}
