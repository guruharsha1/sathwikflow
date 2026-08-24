package com.example.jiralite.auth.api;

import com.example.jiralite.auth.application.AuthService;
import com.example.jiralite.common.security.AppSecurityProperties;
import com.example.jiralite.common.security.CurrentUser;
import com.example.jiralite.common.security.OriginGuard;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service; private final OriginGuard origins; private final CurrentUser currentUser; private final AppSecurityProperties properties;
    public AuthController(AuthService service, OriginGuard origins, CurrentUser currentUser, AppSecurityProperties properties) {
        this.service = service; this.origins = origins; this.currentUser = currentUser; this.properties = properties;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request, HttpServletRequest http) {
        return withSession(HttpStatus.CREATED, service.register(request, agent(http), ip(http)));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request, HttpServletRequest http) {
        return withSession(HttpStatus.OK, service.login(request, agent(http), ip(http)));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.AuthResponse> refresh(HttpServletRequest http) {
        origins.requireTrustedOrigin(http);
        return withSession(HttpStatus.OK, service.refresh(cookie(http), agent(http), ip(http)));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest http) {
        origins.requireTrustedOrigin(http); service.logout(cookie(http));
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, expiredCookie().toString()).build();
    }
    @GetMapping("/me") public AuthDtos.UserResponse me(Authentication authentication) { return service.me(currentUser.id(authentication)); }
    private ResponseEntity<AuthDtos.AuthResponse> withSession(HttpStatus status, AuthService.LoginResult result) {
        return ResponseEntity.status(status).header(HttpHeaders.SET_COOKIE, cookie(result.rawRefreshToken()).toString()).body(result.response());
    }
    private ResponseCookie cookie(String token) { return ResponseCookie.from(properties.getRefreshCookieName(), token).httpOnly(true).secure(properties.isCookieSecure())
            .sameSite("Lax").path("/api/v1/auth").maxAge(Duration.ofDays(properties.getRefreshTokenDays())).build(); }
    private ResponseCookie expiredCookie() { return ResponseCookie.from(properties.getRefreshCookieName(), "").httpOnly(true).secure(properties.isCookieSecure()).sameSite("Lax").path("/api/v1/auth").maxAge(Duration.ZERO).build(); }
    private String cookie(HttpServletRequest request) { if (request.getCookies() == null) return null; for (var cookie : request.getCookies()) if (properties.getRefreshCookieName().equals(cookie.getName())) return cookie.getValue(); return null; }
    private String agent(HttpServletRequest request) { return request.getHeader("User-Agent"); }
    private String ip(HttpServletRequest request) { String forwarded = request.getHeader("X-Forwarded-For"); return forwarded == null ? request.getRemoteAddr() : forwarded.split(",")[0].trim(); }
}

