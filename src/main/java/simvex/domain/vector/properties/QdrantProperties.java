package simvex.domain.vector.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("qdrant")
public record QdrantProperties (
        String apiKey,
        String host,
        int port
) { }
