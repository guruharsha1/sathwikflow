package com.example.jiralite.common.security;

import com.example.jiralite.common.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class OriginGuard {
    private final AppSecurityProperties properties;
    public OriginGuard(AppSecurityProperties properties) { this.properties = properties; }
    public void requireTrustedOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null && !properties.getAllowedOrigins().contains(origin)) {
            throw DomainException.forbidden("This origin is not allowed to use session endpoints");
        }
    }
}

