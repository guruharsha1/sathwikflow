package com.example.jiralite.auth.domain;

import com.example.jiralite.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_sessions")
public class RefreshSession {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private UserAccount user;
    @Column(name = "token_hash", nullable = false, unique = true) private String tokenHash;
    @Column(name = "family_id", nullable = false) private UUID familyId;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "revoked_at") private Instant revokedAt;
    @Column(name = "replaced_by_id") private UUID replacedById;
    @Column(name = "user_agent") private String userAgent;
    @Column(name = "ip_address") private String ipAddress;
    protected RefreshSession() { }
    public RefreshSession(UserAccount user, String hash, UUID familyId, Instant expiresAt, String userAgent, String ipAddress) {
        this.user = user; this.tokenHash = hash; this.familyId = familyId; this.expiresAt = expiresAt; this.userAgent = truncate(userAgent, 255); this.ipAddress = truncate(ipAddress, 64);
    }
    public boolean isActiveAt(Instant now) { return revokedAt == null && expiresAt.isAfter(now); }
    public void revoke() { if (revokedAt == null) revokedAt = Instant.now(); }
    public void replacedBy(UUID replacement) { revoke(); this.replacedById = replacement; }
    private static String truncate(String value, int length) { return value == null ? null : value.substring(0, Math.min(value.length(), length)); }
    public UUID getId() { return id; } public UserAccount getUser() { return user; } public String getTokenHash() { return tokenHash; }
    public UUID getFamilyId() { return familyId; } public Instant getExpiresAt() { return expiresAt; } public Instant getRevokedAt() { return revokedAt; }
}

