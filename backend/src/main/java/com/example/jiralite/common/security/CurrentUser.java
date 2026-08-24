package com.example.jiralite.common.security;

import com.example.jiralite.common.exception.DomainException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public UUID id(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwt)) throw DomainException.unauthorized("A bearer access token is required");
        return UUID.fromString(jwt.getToken().getSubject());
    }
    public boolean isSystemAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
    }
}

