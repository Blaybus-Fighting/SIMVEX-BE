package simvex.global.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;

import java.util.List;

@Slf4j
public class LoggingDocumentRetriever implements DocumentRetriever {

    private static final int DEFAULT_MAX_CHARS = 240;

    private final DocumentRetriever delegate;
    private final int maxChars;

    public LoggingDocumentRetriever(DocumentRetriever delegate) {
        this(delegate, DEFAULT_MAX_CHARS);
    }

    public LoggingDocumentRetriever(DocumentRetriever delegate, int maxChars) {
        this.delegate = delegate;
        this.maxChars = maxChars;
    }

    @Override
    public List<Document> retrieve(Query query) {
        List<Document> documents = delegate.retrieve(query);

        if (log.isInfoEnabled()) {
            Object filter = query.context().get(VectorStoreDocumentRetriever.FILTER_EXPRESSION);
            log.debug("VectorStore retrieved {} docs | query='{}' | filter={}",
                    documents.size(), oneLine(query.text()), filter);

            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                log.debug("VectorStore doc[{}] id={} score={} metadata={} text=\"{}\"",
                        i, doc.getId(), doc.getScore(), doc.getMetadata(), preview(doc));
            }
        }

        return documents;
    }

    private String preview(Document doc) {
        if (!doc.isText()) {
            return "<non-text>";
        }
        String text = doc.getText();
        if (text == null) {
            return "<empty>";
        }
        String singleLine = oneLine(text);
        if (singleLine.length() <= maxChars) {
            return singleLine;
        }
        return singleLine.substring(0, Math.max(0, maxChars - 3)) + "...";
    }

    private String oneLine(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("\\s+", " ").trim();
    }
}
