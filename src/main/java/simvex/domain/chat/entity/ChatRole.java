package simvex.domain.chat.entity;

import lombok.Getter;

@Getter
public enum ChatRole {
    USER("사용자"),
    ASSISTANT("AI"),
    SYSTEM("시스템");

    private final String role;

    ChatRole(String role) {
        this.role = role;
    }
}
