package com.example.jiralite.issue.domain;

import com.example.jiralite.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Locale;
import java.util.UUID;

@Entity @Table(name = "labels")
public class Label {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id") private Project project;
    @Column(nullable = false) private String name;
    @Column(name = "normalized_name", nullable = false) private String normalizedName;
    @Column(nullable = false) private String color;
    protected Label() { }
    public Label(Project project, String name, String color) { this.project = project; this.name = name.trim(); this.normalizedName = name.trim().toLowerCase(Locale.ROOT); this.color = color; }
    public UUID getId() { return id; } public Project getProject() { return project; } public String getName() { return name; } public String getColor() { return color; }
}

