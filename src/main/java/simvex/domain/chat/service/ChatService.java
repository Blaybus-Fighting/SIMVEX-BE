package simvex.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import simvex.domain.chat.dto.ChatMessageDto;
import simvex.domain.chat.dto.ChatRequestDto;
import simvex.domain.chat.dto.SseMessageDto;
import simvex.domain.chat.entity.ChatMessage;
import simvex.domain.chat.entity.ChatRole;
import simvex.domain.chat.repository.ChatMessageRepository;
import simvex.domain.session.entity.Session;
import simvex.domain.session.repository.SessionRepository;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final RetrievalAugmentationAdvisor advisor;
    private final SessionRepository sessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 60L * 1000L; // 1시간
    private static final int DEFAULT_PAGE_SIZE = 10;

    public ChatMessageDto ragChat(ChatRequestDto chatRequestDto) {
        String filterExpression = buildFilterExpression(chatRequestDto.toPayload());

        String content = chatClient.prompt()
                .user(chatRequestDto.question())
                .advisors(advisor)
                .advisors(a -> {
                    if (filterExpression != null && !filterExpression.isBlank()) {
                        a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, filterExpression);
                    }
                })
                .call()
                .content();

        return ChatMessageDto.create(ChatRole.USER, content);
    }

    public SseEmitter streamRagChat(ChatRequestDto chatRequestDto) {
        Session session = sessionRepository.findById(chatRequestDto.sessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        ChatMessage userMessage = ChatMessage.create(session, chatRequestDto.question(), ChatRole.USER);
        chatMessageRepository.save(userMessage);

        String filterExpression = buildFilterExpression(chatRequestDto.toPayload());

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        StringBuilder rawResponse = new StringBuilder();
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();

        AtomicInteger sequence = new AtomicInteger(0);

        sendMessage(emitter, "connect", new SseMessageDto("connect", "SSE Connect", sequence.getAndAdd(1)));

        Flux<String> content = chatClient.prompt()
                .user(chatRequestDto.question())
                .advisors(advisor)
                .advisors(a -> {
                    if (filterExpression != null && !filterExpression.isBlank()) {
                        a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, filterExpression);
                    }
                })
                .stream()
                .content();


        Disposable subscription = content.subscribe(
                chunk -> {
                    rawResponse.append(chunk);
                    sendMessage(emitter, "message", new SseMessageDto("chunk", chunk, sequence.getAndAdd(1)));
                }, error -> {
                    log.error("SSE 스트리밍 에러", error);
                    emitter.completeWithError(error);
                }, () -> {
                    chatMessageRepository.save(ChatMessage.create(session, rawResponse.toString(), ChatRole.ASSISTANT));
                    ChatMessageDto chatMessage = ChatMessageDto.create(ChatRole.ASSISTANT, rawResponse.toString());
                    sendMessage(emitter, "done", chatMessage);
                    emitter.complete();
                });

        subscriptionRef.set(subscription);

        emitter.onCompletion(() -> disposeSubscription(subscriptionRef.get()));
        emitter.onTimeout(() -> disposeSubscription(subscriptionRef.get()));
        emitter.onError(error -> disposeSubscription(subscriptionRef.get()));

        return emitter;
    }

    public Slice<ChatMessageDto> findMessages(Long sessionId, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Slice<ChatMessage> messageSlice = chatMessageRepository.findBySession_IdOrderByCreatedAt(sessionId, pageable);
        log.info("messages = {}", messageSlice);
        return messageSlice.map(ChatMessageDto::trans);
    }

    private void sendMessage(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        }
        catch (IOException e) {
            throw new IllegalStateException("SSE Message 전송 실패", e);
        }
    }

    private void disposeSubscription(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * VectorStoreDocumentRetriever의 FILTER_EXPRESSION용 표현식을 생성합니다.
     * - String 값은 단일 따옴표로 감싸고 내부 따옴표는 escape 처리합니다.
     * - Number/Boolean은 그대로 넣습니다.
     * - 그 외 타입은 toString()으로 문자열 취급합니다.
     *
     * 예) {toolType=SPRING_AI, active=true} -> "toolType == 'SPRING_AI' && active == true"
     */
    private String buildFilterExpression(Map<String, String> metadataFilters) {
        if (metadataFilters == null || metadataFilters.isEmpty()) {
            return null;
        }

        return metadataFilters.entrySet().stream()
                .filter(e -> e.getKey() != null && !e.getKey().isBlank())
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + " == " + toFilterLiteral(e.getValue()))
                .collect(Collectors.joining(" && "));
    }

    private String toFilterLiteral(String value) {
        // 단일따옴표 escape: ' -> \\'
        value = value.replace("'", "\\\\'");
        return "'" + value + "'";
    }
}
