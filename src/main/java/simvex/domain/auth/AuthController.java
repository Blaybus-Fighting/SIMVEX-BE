package simvex.domain.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import simvex.domain.auth.dto.AuthTicketExchangeRequest;
import simvex.domain.auth.dto.AuthTokenResponse;
import simvex.global.auth.ticket.AuthTicketService;
import simvex.global.dto.ApiResponse;
import simvex.global.exception.CustomException;
import simvex.global.exception.ErrorCode;

@Tag(name = "Auth API", description = "인증 API")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthTicketService authTicketService;

    @GetMapping("/oauth/success")
    public ApiResponse<String> successAPI() {
        return ApiResponse.onSuccess("Login Successful");
    }

    @GetMapping("/oauth/fail")
    public ApiResponse<Void> failAPI(@RequestParam(value = "error", required = false) String error) {
        return ApiResponse.onFailure(
                ErrorCode.UNAUTHORIZED.getCode(),
                "Login Failed" + (error != null ? ": " + error : "")
        );
    }

    @PostMapping("/auth/logout")
    public ApiResponse<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ApiResponse.onSuccess("Logout Successful");
    }

    @PostMapping("/auth/exchange")
    public ApiResponse<AuthTokenResponse> exchangeTicket(@RequestBody @Valid AuthTicketExchangeRequest request) {
        String token = authTicketService.consume(request.ticket())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        return ApiResponse.onSuccess(new AuthTokenResponse(token));
    }
}
