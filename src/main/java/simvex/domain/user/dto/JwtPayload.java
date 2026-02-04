package simvex.domain.user.dto;

public record JwtPayload(
        String providerUserId,
        String name,
        String email,
        String role
) {}