package com.example.jiralite.project.application;

import com.example.jiralite.project.api.ProjectController.CreateProjectRequest;
import com.example.jiralite.project.api.ProjectController.ProjectResponse;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.project.persistence.ProjectRepository;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    private final UserRepository users;

    public ProjectService(ProjectRepository projects, UserRepository users) {
        this.projects = projects;
        this.users = users;
    }

    public List<ProjectResponse> list() {
        return projects.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponse get(String projectKey) {
        return projects.findByProjectKey(projectKey.toUpperCase()).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        UserAccount creator = users.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Register a user before creating a project"));
        Project project = projects.save(new Project(request.projectKey(), request.name(), request.description(), creator));
        return toResponse(project);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getProjectKey(), project.getName(), project.getDescription());
    }
}
