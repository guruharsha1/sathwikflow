package com.example.jiralite.user;

import com.example.jiralite.common.auditing.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount extends AuditedEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalRole globalRole = GlobalRole.ROLE_USER;

    @Column(nullable = false)
    private boolean enabled = true;

    protected UserAccount() {
    }

    public UserAccount(String displayName, String email, String passwordHash) {
        this.displayName = displayName;
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public GlobalRole getGlobalRole() {
        return globalRole;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
