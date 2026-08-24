package com.example.jiralite.project.api;

import com.example.jiralite.project.domain.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class ProjectDtos {
    private ProjectDtos() { }
    public record CreateProjectRequest(@NotBlank @Pattern(regexp = "[A-Za-z][A-Za-z0-9]{1,9}", message = "must be 2-10 letters or numbers starting with a letter") String projectKey,
                                       @NotBlank @Size(max = 255) String name, @Size(max = 2000) String description) { }
    public record UpdateProjectRequest(@NotBlank @Size(max = 255) String name, @Size(max = 2000) String description) { }
    public record ProjectResponse(UUID id, String projectKey, String name, String description, ProjectRole myRole, Instant createdAt, Instant updatedAt) { }
    public record AddMemberRequest(@NotNull UUID userId, @NotNull ProjectRole role) { }
    public record ChangeMemberRoleRequest(@NotNull ProjectRole role) { }
    public record MemberResponse(UUID userId, String displayName, String email, ProjectRole role, Instant joinedAt) { }
    public record MemberCandidateResponse(UUID id, String displayName, String email) { }
}

