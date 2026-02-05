package simvex.domain.vector.utils;

import io.qdrant.client.grpc.JsonWithInt;

import java.util.Map;
import java.util.stream.Collectors;

public final class PayloadConverter {

    private PayloadConverter() { }

    public static Map<String, JsonWithInt.Value> toJsonWithIntMap(Map<String, Object> payload) {
        return payload.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toValue(e.getValue())));
    }

    private static JsonWithInt.Value toValue(Object value) {
        if (value == null) {
            return JsonWithInt.Value.newBuilder().setNullValueValue(0).build();
        }
        if (value instanceof Boolean b) {
            return JsonWithInt.Value.newBuilder().setBoolValue(b).build();
        }
        return JsonWithInt.Value.newBuilder().setStringValue(String.valueOf(value)).build();
    }
}
