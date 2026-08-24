package com.example.jiralite.project.domain;

import com.example.jiralite.common.auditing.AuditedEntity;
import com.example.jiralite.user.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @Column(name = "project_key", nullable = false, unique = true, updatable = false) private String projectKey;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String description;
    @Column(name = "next_issue_number", nullable = false) private long nextIssueNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by") private UserAccount createdBy;
    protected Project() { }
    public Project(String key, String name, String description, UserAccount creator) {
        this.projectKey = key; this.name = name.trim(); this.description = blankToNull(description); this.createdBy = creator; this.nextIssueNumber = 1;
    }
    public long allocateIssueNumber() { return nextIssueNumber++; }
    public void update(String name, String description) { this.name = name.trim(); this.description = blankToNull(description); }
    private static String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    public UUID getId() { return id; } public String getProjectKey() { return projectKey; } public String getName() { return name; }
    public String getDescription() { return description; } public long getNextIssueNumber() { return nextIssueNumber; } public UserAccount getCreatedBy() { return createdBy; }
}

