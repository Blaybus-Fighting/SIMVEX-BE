package simvex.domain.vector.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import simvex.domain.vector.domain.AssetCommonPayload;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VectorIngestService {

    private final VectorStore vectorStore;

    /**
     * Content 및 Payload를 Document로 변환 및 임베딩 & VectorDB 저장
     * @param content Embedding 할 내용
     * @param payload Payload(Metadata)
     * @param extraMetadata 추가로 입력할 Payload
     */
    public void ingestText(String content, @NotNull AssetCommonPayload payload, Map<String, Object> extraMetadata) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException(ErrorCode.VECTOR_INVALID_ARGUMENT);
        }

        String documentId = UUID.randomUUID().toString();
        Map<String, Object> metadata = new HashMap<>(payload.toPayload());
        if (extraMetadata != null) {
            metadata.putAll(extraMetadata);
        }
        Document document = new Document(documentId, content, metadata);

        try {
            vectorStore.add(List.of(document));
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.VECTOR_INGEST_FAILED);
        }
    }
}
