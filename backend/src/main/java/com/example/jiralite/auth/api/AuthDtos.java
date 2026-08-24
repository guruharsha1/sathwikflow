package com.example.jiralite.auth.api;

import com.example.jiralite.user.GlobalRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() { }
    public record RegisterRequest(@NotBlank @Size(max = 100) String displayName, @NotBlank @Email @Size(max = 255) String email,
                                  @NotBlank @Size(min = 10, max = 128) String password) { }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record UserResponse(UUID id, String displayName, String email, GlobalRole globalRole) { }
    public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) { }
}

