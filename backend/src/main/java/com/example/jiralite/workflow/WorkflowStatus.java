package com.example.jiralite.workflow;

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
import java.util.UUID;

@Entity
@Table(name = "workflow_statuses")
public class WorkflowStatus {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id") private Project project;
    @Column(nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private WorkflowCategory category;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @Column(nullable = false) private String color;
    @Column(name = "initial_status", nullable = false) private boolean initial;
    @Column(name = "terminal_status", nullable = false) private boolean terminal;
    protected WorkflowStatus() { }
    public WorkflowStatus(Project project, String name, WorkflowCategory category, int displayOrder, String color, boolean initial, boolean terminal) {
        this.project = project; this.name = name.trim(); this.category = category; this.displayOrder = displayOrder; this.color = color; this.initial = initial; this.terminal = terminal;
    }
    public void update(String name, WorkflowCategory category, int order, String color, boolean initial, boolean terminal) {
        this.name = name.trim(); this.category = category; this.displayOrder = order; this.color = color; this.initial = initial; this.terminal = terminal;
    }
    public UUID getId() { return id; } public Project getProject() { return project; } public String getName() { return name; }
    public WorkflowCategory getCategory() { return category; } public int getDisplayOrder() { return displayOrder; } public String getColor() { return color; }
    public boolean isInitial() { return initial; } public boolean isTerminal() { return terminal; }
}

