package simvex.domain.vector.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record UpdatePayloadRequest(
        @NotBlank String entityId,
        @NotEmpty Map<String, Object> payload
) { }
