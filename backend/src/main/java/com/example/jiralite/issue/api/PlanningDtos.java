package com.example.jiralite.issue.api;

import com.example.jiralite.issue.domain.SprintState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class PlanningDtos {
    private PlanningDtos() { }
    public record EpicRequest(@NotBlank @Size(max = 255) String name, @Size(max = 2000) String description, @Pattern(regexp = "#[0-9a-fA-F]{6}") String color) { }
    public record EpicResponse(UUID id, String name, String description, String color, Instant createdAt, Instant updatedAt) { }
    public record SprintRequest(@NotBlank @Size(max = 255) String name, @Size(max = 2000) String goal, LocalDate startDate, LocalDate endDate) { }
    public record SprintResponse(UUID id, String name, String goal, SprintState state, LocalDate startDate, LocalDate endDate, Instant createdAt, Instant updatedAt) { }
    public record LabelRequest(@NotBlank @Size(max = 80) String name, @NotBlank @Pattern(regexp = "#[0-9a-fA-F]{6}") String color) { }
    public record LabelResponse(UUID id, String name, String color) { }
}

