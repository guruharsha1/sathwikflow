package com.example.jiralite.activity.domain;

import com.example.jiralite.issue.domain.Issue;
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

@Entity @Table(name = "activity_events")
public class ActivityEvent {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "issue_id") private Issue issue;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "actor_id") private UserAccount actor;
    @Column(name = "event_type", nullable = false) private String eventType;
    @Column(name = "field_name") private String fieldName;
    @Column(name = "old_value", length = 1000) private String oldValue;
    @Column(name = "new_value", length = 1000) private String newValue;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    protected ActivityEvent() { }
    public ActivityEvent(Issue issue, UserAccount actor, String eventType, String fieldName, String oldValue, String newValue) {
        this.issue = issue; this.actor = actor; this.eventType = eventType; this.fieldName = fieldName; this.oldValue = oldValue; this.newValue = newValue; this.occurredAt = Instant.now();
    }
    public UUID getId() { return id; } public Issue getIssue() { return issue; } public UserAccount getActor() { return actor; }
    public String getEventType() { return eventType; } public String getFieldName() { return fieldName; } public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; } public Instant getOccurredAt() { return occurredAt; }
}

