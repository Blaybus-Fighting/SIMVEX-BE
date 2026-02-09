package simvex.domain.user.dto;

public record JwtPayload(
        Long id,
        String providerUserId,
        String name,
        String email,
        String role,
        String profileImage
) {}
