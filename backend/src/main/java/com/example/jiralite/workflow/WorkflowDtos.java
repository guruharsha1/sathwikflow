package com.example.jiralite.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public final class WorkflowDtos {
    private WorkflowDtos() { }
    public record StatusRequest(@NotBlank @Size(max = 80) String name, @NotNull WorkflowCategory category, @NotBlank @Pattern(regexp = "#[0-9a-fA-F]{6}") String color,
                                boolean initial, boolean terminal, Integer displayOrder) { }
    public record StatusResponse(UUID id, String name, WorkflowCategory category, int displayOrder, String color, boolean initial, boolean terminal) { }
}

