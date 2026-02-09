package simvex.domain.auth.dto;

public record AuthUserResponse(
        Long id,
        String name,
        String profileImage
) {}
