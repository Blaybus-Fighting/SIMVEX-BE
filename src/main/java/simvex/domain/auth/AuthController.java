package simvex.domain.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import simvex.global.dto.ApiResponse;
import simvex.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
public class AuthController {

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
}
