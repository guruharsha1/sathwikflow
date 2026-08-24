package com.example.jiralite.user;

import com.example.jiralite.common.auditing.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name = "display_name", nullable = false) private String displayName;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "password_hash", nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(name = "global_role", nullable = false) private GlobalRole globalRole;
    @Column(nullable = false) private boolean enabled;
    protected UserAccount() { }
    public UserAccount(String displayName, String email, String passwordHash) {
        this.displayName = displayName.trim(); this.email = email.trim().toLowerCase(Locale.ROOT); this.passwordHash = passwordHash;
        this.globalRole = GlobalRole.USER; this.enabled = true;
    }
    public UUID getId() { return id; } public String getDisplayName() { return displayName; } public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; } public GlobalRole getGlobalRole() { return globalRole; } public boolean isEnabled() { return enabled; }
    public void disable() { enabled = false; }
}

