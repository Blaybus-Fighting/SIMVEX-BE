package simvex.domain.vector.ingest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MachineInfo(
        @JsonProperty("definition") String definition,
        @JsonProperty("material_overview") String materialOverview,
        @JsonProperty("role_and_usage") String roleAndUsage,
        @JsonProperty("role_and_principle") String roleAndPrinciple,
        @JsonProperty("function") String function,
        @JsonProperty("system_impact") String systemImpact
) {
}
