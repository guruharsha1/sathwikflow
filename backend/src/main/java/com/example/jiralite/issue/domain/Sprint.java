package com.example.jiralite.issue.domain;

import com.example.jiralite.common.auditing.AuditedEntity;
import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "sprints")
public class Sprint extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id") private Project project;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String goal;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private SprintState state;
    private LocalDate startDate; private LocalDate endDate;
    @Column(name = "active_slot") private Integer activeSlot;
    protected Sprint() { }
    public Sprint(Project project, String name, String goal, LocalDate startDate, LocalDate endDate) {
        this.project = project; this.state = SprintState.PLANNED; update(name, goal, startDate, endDate);
    }
    public void update(String name, String goal, LocalDate startDate, LocalDate endDate) {
        if (state == SprintState.COMPLETED) throw DomainException.validation("Completed sprints cannot be edited");
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) throw DomainException.validation("Sprint end date must be after its start date");
        this.name = name.trim(); this.goal = goal == null || goal.isBlank() ? null : goal.trim(); this.startDate = startDate; this.endDate = endDate;
    }
    public void start() { if (state != SprintState.PLANNED) throw DomainException.validation("Only planned sprints can be started"); state = SprintState.ACTIVE; activeSlot = 1; if (startDate == null) startDate = LocalDate.now(); }
    public void complete() { if (state != SprintState.ACTIVE) throw DomainException.validation("Only active sprints can be completed"); state = SprintState.COMPLETED; activeSlot = null; if (endDate == null) endDate = LocalDate.now(); }
    public UUID getId() { return id; } public Project getProject() { return project; } public String getName() { return name; } public String getGoal() { return goal; }
    public SprintState getState() { return state; } public LocalDate getStartDate() { return startDate; } public LocalDate getEndDate() { return endDate; } public Integer getActiveSlot() { return activeSlot; }
}

