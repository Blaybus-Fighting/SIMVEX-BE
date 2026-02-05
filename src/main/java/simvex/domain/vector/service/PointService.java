package simvex.domain.vector.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import simvex.domain.vector.constants.PayloadKey;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.util.StringUtils;

import static io.qdrant.client.ConditionFactory.matchKeyword;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;
import static simvex.domain.vector.constants.QdrantCollectionConst.COLLECTION;

@Service
public class PointService {

    private final QdrantClient qdrantClient;

    public PointService(VectorStore vectorStore) {
        Object nativeClient = vectorStore.getNativeClient()
                .orElseThrow(() -> new CustomException(ErrorCode.VECTOR_STORE_INITIALIZATION_FAILED));

        if (!(nativeClient instanceof QdrantClient)) {
            throw new CustomException(ErrorCode.VECTOR_STORE_INITIALIZATION_FAILED);
        }
        this.qdrantClient = (QdrantClient) nativeClient;
    }

    /**
     * 해당 EntityId를 가진 Point의 Payload Update
     * @param entityId Point의 entityId
     * @param payload 업데이트 할 payload(metadata)
     * @return 업데이트 결과
     */
    public Points.UpdateResult updatePayload(String entityId, Map<String, JsonWithInt.Value> payload) {
        if (!StringUtils.hasText(entityId) || payload == null || payload.isEmpty()) {
            throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
        }

        try {
            Points.ScrollPoints scrollPoints = Points.ScrollPoints.newBuilder()
                    .setCollectionName(COLLECTION)
                    .setFilter(
                            Points.Filter.newBuilder()
                                    .addMust(matchKeyword(PayloadKey.ENTITY_ID, entityId))
                                    .build()
                    )
                    .setLimit(1)
                    .setWithPayload(enable(false))
                    .build();

            Points.ScrollResponse scrollResponse = qdrantClient.scrollAsync(scrollPoints).get();
            List<Points.RetrievedPoint> points = scrollResponse.getResultList();

            if (points.isEmpty()) {
                throw new CustomException(ErrorCode.POINT_NOT_FOUND);
            }
            if (points.size() > 1) {
                throw new CustomException(ErrorCode.DUPLICATED_ENTITY_ID);
            }

            Points.PointId pointId = points.get(0).getId();

            return qdrantClient.overwritePayloadAsync(
                    COLLECTION,
                    payload,
                    pointId,
                    true,
                    null,
                    null
            ).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.QDRANT_COMMUNICATION_ERROR);
        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.QDRANT_COMMUNICATION_ERROR);
        }
    }

    /**
     * Point에 Payload(Metadata) 추가
     * @param index 추가할 Point의 Payload (id)
     */
    public void createPayloadIndex(String index) {
        if (!StringUtils.hasText(index)) {
            throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
        }

        try {
            qdrantClient.createPayloadIndexAsync(
                    COLLECTION,
                    index,
                    Collections.PayloadSchemaType.Keyword,
                    null,
                    true,
                    null,
                    null
            ).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.QDRANT_COMMUNICATION_ERROR);
        } catch (ExecutionException e) {
            throw new CustomException(ErrorCode.QDRANT_COMMUNICATION_ERROR);
        }
    }
}
