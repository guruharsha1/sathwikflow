package com.example.jiralite.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity @Table(name = "notification_outbox")
public class NotificationOutbox {
    @Id @GeneratedValue private UUID id;
    @Column(name = "notification_type", nullable = false) private String notificationType;
    @Column(name = "recipient_email", nullable = false) private String recipientEmail;
    @Column(nullable = false) private String subject;
    @Column(name = "body_text", nullable = false, length = 5000) private String bodyText;
    @Column(name = "issue_key") private String issueKey;
    @Column(name = "issue_url") private String issueUrl;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationStatus status;
    @Column(nullable = false) private int attempts;
    @Column(name = "next_attempt_at") private Instant nextAttemptAt;
    @Column(name = "last_error", length = 1000) private String lastError;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "sent_at") private Instant sentAt;
    protected NotificationOutbox() { }
    public NotificationOutbox(String type, String email, String subject, String body, String issueKey, String issueUrl) {
        this.notificationType = type; this.recipientEmail = email; this.subject = subject; this.bodyText = body; this.issueKey = issueKey; this.issueUrl = issueUrl;
        this.status = NotificationStatus.PENDING; this.attempts = 0; this.createdAt = Instant.now();
    }
    public void sent() { status = NotificationStatus.SENT; sentAt = Instant.now(); nextAttemptAt = null; lastError = null; }
    public void failed(Exception exception) { attempts++; status = NotificationStatus.FAILED; lastError = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage().substring(0, Math.min(1000, exception.getMessage().length())); nextAttemptAt = Instant.now().plus(Math.min(60, 1L << Math.min(attempts, 6)), ChronoUnit.MINUTES); }
    public UUID getId() { return id; } public String getRecipientEmail() { return recipientEmail; } public String getSubject() { return subject; }
    public String getBodyText() { return bodyText; } public NotificationStatus getStatus() { return status; } public int getAttempts() { return attempts; }
}

