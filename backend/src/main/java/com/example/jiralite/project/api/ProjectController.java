package com.example.jiralite.project.api;

import com.example.jiralite.project.application.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projects;

    ProjectController(ProjectService projects) {
        this.projects = projects;
    }

    @GetMapping
    List<ProjectResponse> list() {
        return projects.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projects.create(request);
    }

    @GetMapping("/{projectKey}")
    ProjectResponse get(@PathVariable String projectKey) {
        return projects.get(projectKey);
    }

    public record CreateProjectRequest(
            @Pattern(regexp = "^[A-Z][A-Z0-9]{1,11}$") String projectKey,
            @NotBlank String name,
            String description) {
    }

    public record ProjectResponse(String projectKey, String name, String description) {
    }
}
