package simvex.global.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ThresholdContextQueryAugmenter implements QueryAugmenter {

    private final ContextualQueryAugmenter delegate;
    private final PromptTemplate lowContextPromptTemplate;
    private final int minDocuments;
    private final int minTotalChars;
    @Nullable
    private final Double minScore;

    public ThresholdContextQueryAugmenter(
            ContextualQueryAugmenter delegate,
            PromptTemplate lowContextPromptTemplate,
            int minDocuments,
            int minTotalChars,
            @Nullable Double minScore
    ) {
        this.delegate = delegate;
        this.lowContextPromptTemplate = lowContextPromptTemplate;
        this.minDocuments = Math.max(0, minDocuments);
        this.minTotalChars = Math.max(0, minTotalChars);
        this.minScore = minScore;
    }

    @Override
    public Query augment(Query query, List<Document> documents) {
        Assert.notNull(query, "query cannot be null");
        Assert.notNull(documents, "documents cannot be null");

        if (isInsufficient(documents)) {
            if (log.isInfoEnabled()) {
                log.info("Context below threshold: docs={}, totalChars={}, maxScore={}",
                        documents.size(), totalChars(documents), maxScore(documents));
            }
            return new Query(lowContextPromptTemplate.render(Map.of("query", query.text())));
        }

        return delegate.augment(query, documents);
    }

    private boolean isInsufficient(List<Document> documents) {
        if (documents.isEmpty()) {
            return true;
        }
        if (this.minDocuments > 0 && documents.size() < this.minDocuments) {
            return true;
        }
        if (this.minTotalChars > 0 && totalChars(documents) < this.minTotalChars) {
            return true;
        }
        if (this.minScore != null && this.minScore > 0) {
            double maxScore = maxScore(documents);
            if (Double.isNaN(maxScore) || maxScore < this.minScore) {
                return true;
            }
        }
        return false;
    }

    private int totalChars(List<Document> documents) {
        int total = 0;
        for (Document document : documents) {
            if (document != null && document.isText()) {
                String text = document.getText();
                if (StringUtils.hasText(text)) {
                    total += text.length();
                }
            }
        }
        return total;
    }

    private double maxScore(List<Document> documents) {
        return documents.stream()
                .map(Document::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(Double.NaN);
    }
}
