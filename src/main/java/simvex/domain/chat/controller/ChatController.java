package simvex.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import simvex.domain.chat.dto.ChatMessageDto;
import simvex.domain.chat.dto.ChatRequestDto;
import simvex.domain.chat.service.ChatService;
import simvex.global.auth.oauth2.user.PrincipalOAuth2User;
import simvex.global.dto.ApiResponse;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "LLM 채팅 및 대화 기록 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "LLM 채팅", description = "RAG 기반으로 단일 응답을 생성합니다.")
    public ApiResponse<ChatMessageDto> chatLLM(@RequestBody ChatRequestDto chatRequestDto) {
        return ApiResponse.onSuccess(chatService.ragChat(chatRequestDto));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "LLM 채팅 스트리밍", description = "RAG 기반으로 SSE 스트리밍 응답을 제공합니다.")
    public SseEmitter chatLLMStream(@RequestBody ChatRequestDto chatRequestDto) {
        return chatService.streamRagChat(chatRequestDto);
    }

    @GetMapping("/{sessionId}/messages")
    @Operation(summary = "대화 메시지 조회", description = "세션의 대화 메시지를 페이지 단위로 조회합니다.")
    public ApiResponse<Slice<ChatMessageDto>> getMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal PrincipalOAuth2User principal,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ApiResponse.onSuccess(chatService.findMessages(principal.getId(), sessionId, page));
    }
}
