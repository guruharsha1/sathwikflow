package com.example.jiralite.issue.domain;

import com.example.jiralite.common.auditing.AuditedEntity;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.workflow.WorkflowStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "issues")
public class Issue extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id") private Project project;
    @Column(name = "issue_number", nullable = false) private long issueNumber;
    @Column(nullable = false) private String title;
    @Column(length = 5000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private IssueType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private IssuePriority priority;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "status_id") private WorkflowStatus status;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "reporter_id") private UserAccount reporter;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assignee_id") private UserAccount assignee;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "epic_id") private Epic epic;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sprint_id") private Sprint sprint;
    @ManyToMany @JoinTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
    private Set<Label> labels = new LinkedHashSet<>();
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "board_position", nullable = false) private int boardPosition;
    @Version private long version;
    protected Issue() { }
    public Issue(Project project, long number, String title, String description, IssueType type, IssuePriority priority, WorkflowStatus status,
                 UserAccount reporter, UserAccount assignee, Epic epic, Sprint sprint, LocalDate dueDate, int position, Set<Label> labels) {
        this.project = project; this.issueNumber = number; this.reporter = reporter; this.status = status; this.boardPosition = position;
        update(title, description, type, priority, assignee, epic, sprint, dueDate, labels);
    }
    public void update(String title, String description, IssueType type, IssuePriority priority, UserAccount assignee, Epic epic, Sprint sprint, LocalDate dueDate, Set<Label> labels) {
        this.title = title.trim(); this.description = description == null || description.isBlank() ? null : description.trim(); this.type = type; this.priority = priority;
        this.assignee = assignee; this.epic = epic; this.sprint = sprint; this.dueDate = dueDate; this.labels.clear(); if (labels != null) this.labels.addAll(labels);
    }
    public void moveTo(WorkflowStatus newStatus, int position) { this.status = newStatus; this.boardPosition = position; }
    public void setBoardPosition(int value) { boardPosition = value; }
    public UUID getId() { return id; } public Project getProject() { return project; } public long getIssueNumber() { return issueNumber; }
    public String getIssueKey() { return project.getProjectKey() + "-" + issueNumber; } public String getTitle() { return title; } public String getDescription() { return description; }
    public IssueType getType() { return type; } public IssuePriority getPriority() { return priority; } public WorkflowStatus getStatus() { return status; }
    public UserAccount getReporter() { return reporter; } public UserAccount getAssignee() { return assignee; } public Epic getEpic() { return epic; } public Sprint getSprint() { return sprint; }
    public Set<Label> getLabels() { return Set.copyOf(labels); } public LocalDate getDueDate() { return dueDate; } public int getBoardPosition() { return boardPosition; } public long getVersion() { return version; }
}

