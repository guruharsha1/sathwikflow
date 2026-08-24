package com.example.jiralite.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjectMemberId implements Serializable {
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "user_id") private UUID userId;
    protected ProjectMemberId() { }
    public ProjectMemberId(UUID projectId, UUID userId) { this.projectId = projectId; this.userId = userId; }
    public UUID getProjectId() { return projectId; } public UUID getUserId() { return userId; }
    @Override public boolean equals(Object other) { return other instanceof ProjectMemberId id && Objects.equals(projectId, id.projectId) && Objects.equals(userId, id.userId); }
    @Override public int hashCode() { return Objects.hash(projectId, userId); }
}

