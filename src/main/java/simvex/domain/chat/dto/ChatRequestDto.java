package simvex.domain.chat.dto;

import simvex.domain.vector.constants.PayloadKey;
import simvex.domain.vector.enums.ToolType;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public record ChatRequestDto (
    @NotNull String question,
    ToolType toolType,
    String entityId,
    Long sessionId
) {
    public Map<String, String> toPayload() {
        Map<String, String> payload = new HashMap<>(8);
        if (toolType != null) {
            payload.put(PayloadKey.TOOL_TYPE, toolType.name());
        }
        if (entityId != null) {
            payload.put(PayloadKey.ENTITY_ID, entityId);
        }
        return payload;
    }
}
