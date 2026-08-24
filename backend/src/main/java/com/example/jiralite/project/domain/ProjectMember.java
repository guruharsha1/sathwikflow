package com.example.jiralite.project.domain;

import com.example.jiralite.user.UserAccount;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "project_members")
public class ProjectMember {
    @EmbeddedId private ProjectMemberId id;
    @ManyToOne(fetch = FetchType.LAZY) @MapsId("projectId") @JoinColumn(name = "project_id") private Project project;
    @ManyToOne(fetch = FetchType.LAZY) @MapsId("userId") @JoinColumn(name = "user_id") private UserAccount user;
    @Enumerated(EnumType.STRING) private ProjectRole role;
    private Instant joinedAt;
    protected ProjectMember() { }
    public ProjectMember(Project project, UserAccount user, ProjectRole role) {
        this.id = new ProjectMemberId(project.getId(), user.getId()); this.project = project; this.user = user; this.role = role; this.joinedAt = Instant.now();
    }
    public void changeRole(ProjectRole role) { this.role = role; }
    public ProjectMemberId getId() { return id; } public Project getProject() { return project; } public UserAccount getUser() { return user; }
    public ProjectRole getRole() { return role; } public Instant getJoinedAt() { return joinedAt; }
}

