package simvex.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import simvex.domain.modelobject.entity.ModelObject;
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

    public ChatMessageDto ragChat(Long userId, ChatRequestDto chatRequestDto) {
        if (chatRequestDto.sessionId() != null) {
            sessionRepository.findByIdAndUserId(chatRequestDto.sessionId(), userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        }

        String content = chatClient.prompt()
                .user(chatRequestDto.question())
                .advisors(advisor)
                .call()
                .content();

        return ChatMessageDto.create(ChatRole.ASSISTANT, content);
    }

    public SseEmitter streamRagChat(Long userId, ChatRequestDto chatRequestDto) {
        Session session = sessionRepository.findByIdAndUserId(chatRequestDto.sessionId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        ChatMessage userMessage = ChatMessage.create(session, chatRequestDto.question(), ChatRole.USER);
        chatMessageRepository.save(userMessage);

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        StringBuilder rawResponse = new StringBuilder();
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();

        AtomicInteger sequence = new AtomicInteger(0);

        sendMessage(emitter, "connect", new SseMessageDto("connect", "SSE Connect", sequence.getAndAdd(1)));

        Flux<String> content = chatClient.prompt()
                .user(chatRequestDto.question())
                .advisors(advisor)
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

    public Slice<ChatMessageDto> findMessages(Long userId, Long sessionId, int page) {
        if (!sessionRepository.existsByUser_IdAndId(userId, sessionId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }
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
}
