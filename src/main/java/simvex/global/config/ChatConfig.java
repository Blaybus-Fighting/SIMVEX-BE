package simvex.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class ChatConfig {

    private final ResourceLoader resourceLoader;
    private final String systemPromptsPath;

    public ChatConfig(ResourceLoader resourceLoader, @Value("${spring.ai.prompt.system-path}") String systemPromptsPath) {
        this.resourceLoader = resourceLoader;
        this.systemPromptsPath = systemPromptsPath;
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        String systemPrompt = resolveSystemPrompt();
        return ChatClient.builder(chatModel)
                .defaultSystem(s -> s.text(systemPrompt))
                .build();
    }

    @Bean
    public RetrievalAugmentationAdvisor advisor(VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .build()
                )
                .build();
    }

    private String resolveSystemPrompt() {
        if (systemPromptsPath != null && !systemPromptsPath.isBlank()) {
            try {
                Resource resource = resourceLoader.getResource(systemPromptsPath);
                if (!resource.exists()) {
                    log.error("System prompt resource가 해당 경로에 존재하지 않습니다!: {}", systemPromptsPath);
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                }
                try (var inputStream = resource.getInputStream()) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                log.error("System prompt resource를 읽을 수 없습니다!: {}", systemPromptsPath, e);
                throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
            }
        }
        return "";
    }
}
