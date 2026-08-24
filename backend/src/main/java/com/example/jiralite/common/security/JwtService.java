package com.example.jiralite.common.security;

import com.example.jiralite.user.UserAccount;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final AppSecurityProperties properties;
    public JwtService(JwtEncoder encoder, AppSecurityProperties properties) { this.encoder = encoder; this.properties = properties; }
    public IssuedToken issue(UserAccount user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(properties.getIssuer()).audience(java.util.List.of(properties.getAudience()))
                .subject(user.getId().toString()).issuedAt(issuedAt).expiresAt(expiresAt).id(UUID.randomUUID().toString())
                .claim("global_role", user.getGlobalRole().name()).claim("display_name", user.getDisplayName()).build();
        return new IssuedToken(encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(), expiresAt);
    }
    public record IssuedToken(String value, Instant expiresAt) { }
}

