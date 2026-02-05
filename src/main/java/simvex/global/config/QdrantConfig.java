package simvex.global.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import simvex.domain.vector.properties.QdrantProperties;

import static simvex.domain.vector.constants.QdrantCollectionConst.COLLECTION;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(QdrantProperties.class)
public class QdrantConfig {

    private final QdrantProperties qdrantProperties;

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(
                                qdrantProperties.host(),
                                qdrantProperties.port(),
                                true
                        )
                        .withApiKey(qdrantProperties.apiKey())
                        .build()
        );
    }

    @Bean
    public VectorStore vectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName(COLLECTION)
                .initializeSchema(true)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }
}