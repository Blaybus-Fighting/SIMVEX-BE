package simvex.domain.vector.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import simvex.domain.vector.domain.AssetCommonPayload;

public record TextIngestRequest(
        @NotBlank String content,
        @NotNull AssetCommonPayload payload
) { }
