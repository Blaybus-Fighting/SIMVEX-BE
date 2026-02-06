package simvex.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_001", "서버 내부 오류가 발생했습니다."),

    // Common Exception
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값이 유효하지 않습니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "COMMON_002", "페이지 번호나 크기가 올바르지 않습니다. (size는 최대 6)"),

    // Auth Exception
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_001", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_003", "로그인이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_004", "해당 유저를 찾을수 없습니다."),

    // Memo Exception
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMO_001", "메모를 찾을 수 없습니다."),

    // Session Exception
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_001", "세션을 찾을 수 없습니다."),
    SESSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "SESSION_002", "세션 접근 권한이 없습니다."),

    // Model Exception
    MODEL_OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "MODEL_001", "모델을 찾을 수 없습니다."),

    // Part Exception
    PART_NOT_FOUND(HttpStatus.NOT_FOUND, "PART_001", "부품을 찾을 수 없습니다."),

    // Quiz Exception
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "QUIZ_001", "퀴즈를 찾을 수 없습니다."),

    // Vector DB
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "VECTOR_001", "요청한 포인트를 찾을 수 없습니다."),
    DUPLICATED_ENTITY_ID(HttpStatus.CONFLICT, "VECTOR_002", "동일한 entity_id가 여러 포인트에 매핑되어 있습니다."),
    QDRANT_COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_003", "벡터 저장소와 통신 중 오류가 발생했습니다."),
    VECTOR_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "VECTOR_004", "벡터 요청 파라미터가 올바르지 않습니다."),
    VECTOR_STORE_INITIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_005", "벡터 저장소 클라이언트 초기화에 실패했습니다."),
    VECTOR_INGEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VECTOR_006", "벡터 데이터 적재 중 오류가 발생했습니다.");

    private final HttpStatus status;    // HTTP 상태
    private final String code;          // API 응답에 사용할 커스텀 에러 코드 (HTTP 상태 코드와 동일하게)
    private final String message;       // API 응답에 사용할 에러 메시지
}
