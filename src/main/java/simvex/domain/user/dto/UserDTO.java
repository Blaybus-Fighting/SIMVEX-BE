package simvex.domain.user.dto;

public record UserDTO(
        Long id,
        String providerUserId,
        String name,
        String email,
        String role
) {}