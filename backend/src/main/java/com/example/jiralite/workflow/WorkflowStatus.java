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
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowCategory category;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private boolean initialStatus;

    @Column(nullable = false)
    private boolean terminalStatus;

    protected WorkflowStatus() {
    }
}
