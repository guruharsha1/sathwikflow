package com.example.jiralite.issue.domain;

import com.example.jiralite.common.auditing.AuditedEntity;
import com.example.jiralite.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity @Table(name = "epics")
public class Epic extends AuditedEntity {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id") private Project project;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String description;
    private String color;
    protected Epic() { }
    public Epic(Project project, String name, String description, String color) { this.project = project; update(name, description, color); }
    public void update(String name, String description, String color) { this.name = name.trim(); this.description = blankToNull(description); this.color = blankToNull(color); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    public UUID getId() { return id; } public Project getProject() { return project; } public String getName() { return name; }
    public String getDescription() { return description; } public String getColor() { return color; }
}

