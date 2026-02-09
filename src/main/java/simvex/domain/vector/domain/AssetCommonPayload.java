package simvex.domain.vector.domain;

import simvex.domain.vector.constants.PayloadKey;
import simvex.domain.vector.enums.DocType;
import simvex.domain.vector.enums.ToolType;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public record AssetCommonPayload(
        @NotNull DocType docType,
        @NotNull ToolType toolType,
        @NotNull String entityId,
        @NotNull int version,
        @NotNull String source,
        @NotNull Instant createdAt
) {
    public Map<String, Object> toPayload() {
        Map<String, Object> payload = new HashMap<>(8);
        payload.put(PayloadKey.DOC_TYPE, docType.name());
        payload.put(PayloadKey.TOOL_TYPE, toolType.name());
        payload.put(PayloadKey.ENTITY_ID, entityId);
        payload.put(PayloadKey.VERSION, version);
        payload.put(PayloadKey.SOURCE, source);
        payload.put(PayloadKey.CREATED_AT, createdAt.toEpochMilli());
        return payload;
    }
}
