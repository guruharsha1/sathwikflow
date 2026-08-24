package com.example.jiralite.comment;

import com.example.jiralite.common.auditing.AuditedEntity;
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

@Entity @Table(name = "comments")
public class Comment extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "issue_id") private Issue issue;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "author_id") private UserAccount author;
    @Column(nullable = false, length = 5000) private String body;
    @Column(name = "deleted_at") private Instant deletedAt;
    protected Comment() { }
    public Comment(Issue issue, UserAccount author, String body) { this.issue = issue; this.author = author; this.body = body.trim(); }
    public void update(String body) { this.body = body.trim(); }
    public UUID getId() { return id; } public Issue getIssue() { return issue; } public UserAccount getAuthor() { return author; } public String getBody() { return body; } public Instant getDeletedAt() { return deletedAt; }
}

