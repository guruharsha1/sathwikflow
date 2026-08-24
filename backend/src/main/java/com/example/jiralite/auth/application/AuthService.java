package com.example.jiralite.auth.application;

import com.example.jiralite.auth.api.AuthDtos;
import com.example.jiralite.auth.domain.RefreshSession;
import com.example.jiralite.auth.persistence.RefreshSessionRepository;
import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.common.security.AppSecurityProperties;
import com.example.jiralite.common.security.JwtService;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final RefreshSessionRepository sessions;
    private final PasswordEncoder passwords;
    private final JwtService jwt;
    private final AppSecurityProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();
    public AuthService(UserRepository users, RefreshSessionRepository sessions, PasswordEncoder passwords, JwtService jwt, AppSecurityProperties properties) {
        this.users = users; this.sessions = sessions; this.passwords = passwords; this.jwt = jwt; this.properties = properties;
    }
    @Transactional
    public LoginResult register(AuthDtos.RegisterRequest request, String agent, String ip) {
        String email = normalized(request.email());
        if (users.existsByEmailIgnoreCase(email)) throw DomainException.conflict("An account with this email already exists");
        UserAccount user = users.save(new UserAccount(request.displayName(), email, passwords.encode(request.password())));
        return createLogin(user, agent, ip);
    }
    @Transactional
    public LoginResult login(AuthDtos.LoginRequest request, String agent, String ip) {
        UserAccount user = users.findByEmailIgnoreCase(normalized(request.email()))
                .orElseThrow(() -> DomainException.unauthorized("Invalid email or password"));
        if (!user.isEnabled() || !passwords.matches(request.password(), user.getPasswordHash())) throw DomainException.unauthorized("Invalid email or password");
        return createLogin(user, agent, ip);
    }
    @Transactional
    public LoginResult refresh(String rawToken, String agent, String ip) {
        if (rawToken == null || rawToken.isBlank()) throw DomainException.unauthorized("Refresh session is missing");
        RefreshSession previous = sessions.findByTokenHash(hash(rawToken)).orElseThrow(() -> DomainException.unauthorized("Refresh session is invalid"));
        Instant now = Instant.now();
        if (!previous.isActiveAt(now)) {
            if (previous.getRevokedAt() != null) sessions.revokeFamily(previous.getFamilyId());
            throw DomainException.unauthorized("Refresh session is expired or has already been used");
        }
        if (!previous.getUser().isEnabled()) throw DomainException.unauthorized("Account is disabled");
        String replacementRaw = rawToken();
        RefreshSession replacement = sessions.save(new RefreshSession(previous.getUser(), hash(replacementRaw), previous.getFamilyId(),
                now.plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS), agent, ip));
        previous.replacedBy(replacement.getId());
        return response(previous.getUser(), replacementRaw);
    }
    @Transactional
    public void logout(String rawToken) {
        if (rawToken != null && !rawToken.isBlank()) sessions.findByTokenHash(hash(rawToken)).ifPresent(RefreshSession::revoke);
    }
    @Transactional
    public AuthDtos.UserResponse me(UUID id) { return toUser(users.findById(id).orElseThrow(() -> DomainException.unauthorized("Account no longer exists"))); }
    private LoginResult createLogin(UserAccount user, String agent, String ip) {
        String raw = rawToken();
        sessions.save(new RefreshSession(user, hash(raw), UUID.randomUUID(), Instant.now().plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS), agent, ip));
        return response(user, raw);
    }
    private LoginResult response(UserAccount user, String rawRefresh) {
        JwtService.IssuedToken token = jwt.issue(user);
        return new LoginResult(new AuthDtos.AuthResponse(token.value(), "Bearer", Math.max(1, token.expiresAt().getEpochSecond() - Instant.now().getEpochSecond()), toUser(user)), rawRefresh);
    }
    private String rawToken() { byte[] bytes = new byte[32]; secureRandom.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private String hash(String raw) {
        try { return Base64.getUrlEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception exception) { throw new IllegalStateException("SHA-256 unavailable", exception); }
    }
    private String normalized(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    private AuthDtos.UserResponse toUser(UserAccount user) { return new AuthDtos.UserResponse(user.getId(), user.getDisplayName(), user.getEmail(), user.getGlobalRole()); }
    public record LoginResult(AuthDtos.AuthResponse response, String rawRefreshToken) { }
}

