package simvex.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;
import simvex.global.rag.LoggingDocumentRetriever;
import simvex.global.rag.ThresholdContextQueryAugmenter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class ChatConfig {

    private final ResourceLoader resourceLoader;
    private final String systemPromptsPath;
    private final String emptyContextPromptsPath;

    public ChatConfig(
            ResourceLoader resourceLoader,
            @Value("${spring.ai.prompt.system-path}") String systemPromptsPath,
            @Value("${spring.ai.prompt.empty-context-path}") String emptyContextPromptsPath
    ) {
        this.resourceLoader = resourceLoader;
        this.systemPromptsPath = systemPromptsPath;
        this.emptyContextPromptsPath = emptyContextPromptsPath;
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        String systemPrompt = resolvePrompt(systemPromptsPath);
        return ChatClient.builder(chatModel)
                .defaultSystem(s -> s.text(systemPrompt))
                .build();
    }

    @Bean
    public ChatClient.Builder basic(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().temperature(1.0).build());
    }

    /**
     * 참고 Document : <a href="https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html">Spring AI RAG</a>
     */
    @Bean
    public RetrievalAugmentationAdvisor advisor(
            ChatClient.Builder basic,
            VectorStore vectorStore,
            @Value("${spring.ai.rag.min-docs:2}") int minDocs,
            @Value("${spring.ai.rag.min-total-chars:400}") int minTotalChars,
            @Value("${spring.ai.rag.min-score:0}") double minScore
    ) {
        String emptyContextPromptContext = resolvePrompt(emptyContextPromptsPath);

        PromptTemplate emptyContextPrompt = PromptTemplate.builder()
                .template(emptyContextPromptContext)
                .build();

        // 컨텍스트가 충분할 때만 RAG 프롬프트를 사용하고, 부족하면 emptyContextPrompt로 fallback
        ContextualQueryAugmenter baseAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .build();
        QueryAugmenter queryAugmenter = new ThresholdContextQueryAugmenter(
                baseAugmenter,
                emptyContextPrompt,
                minDocs,
                minTotalChars,
                minScore > 0 ? minScore : null
        );

        // VectorStore Similary Query 검색 설정
        DocumentRetriever retriever = new LoggingDocumentRetriever(
                VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(6)
                .similarityThreshold(0.50)
                .build()
        );

        // RewriteQueryTransformer는 벡터 스토어나 웹 검색 엔진과 같은 대상 시스템을 조회할 때 더 나은 결과를 제공하기 위해 사용자 쿼리를 재작성하기 위해 대규모 언어 모델을 사용합니다.
        // 이 변환기는 사용자 쿼리가 장황하거나 모호하거나 검색 결과 품질에 영향을 줄 수 있는 관련 없는 정보를 포함하고 있을 때 유용합니다.
        RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(basic)
                .build();


        // MultiQueryExpander는 대규모 언어 모델을 사용하여 쿼리를 여러 의미적으로 다양한 변형으로 확장함으로써 다양한 관점을 포착하고,
        // 추가적인 맥락 정보를 검색하고 관련 결과를 찾을 가능성을 높이는 데 유용합니다.
        MultiQueryExpander expander = MultiQueryExpander.builder()
                .chatClientBuilder(basic)
                .numberOfQueries(3)
                .includeOriginal(true)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(rewriteQueryTransformer)
                .queryExpander(expander)
                .documentRetriever(retriever)
                .queryAugmenter(queryAugmenter)
                .build();
    }

    private String resolvePrompt(String promptPath) {
        if (promptPath != null && !promptPath.isBlank()) {
            try {
                Resource resource = resourceLoader.getResource(promptPath);
                if (!resource.exists()) {
                    log.error("Prompt resource가 해당 경로에 존재하지 않습니다!: {}", promptPath);
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                }
                try (var inputStream = resource.getInputStream()) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                log.error("Prompt resource를 읽을 수 없습니다!: {}", promptPath, e);
                throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
            }
        }
        return "";
    }
}
