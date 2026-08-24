package com.example.jiralite.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_outbox")
public class NotificationOutbox {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 5000)
    private String bodyText;

    private String issueKey;
    private String issueUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(nullable = false)
    private int attempts;

    private Instant nextAttemptAt;
    private String lastError;
    private Instant createdAt = Instant.now();
    private Instant sentAt;
}
