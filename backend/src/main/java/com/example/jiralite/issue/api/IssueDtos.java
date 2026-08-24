package com.example.jiralite.issue.api;

import com.example.jiralite.issue.domain.IssuePriority;
import com.example.jiralite.issue.domain.IssueType;
import com.example.jiralite.workflow.WorkflowCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class IssueDtos {
    private IssueDtos() { }
    public record CreateIssueRequest(@NotBlank @Size(max = 255) String title, @Size(max = 5000) String description, @NotNull IssueType type, @NotNull IssuePriority priority,
                                     UUID statusId, UUID assigneeId, UUID epicId, UUID sprintId, LocalDate dueDate, List<UUID> labelIds) { }
    public record UpdateIssueRequest(@NotBlank @Size(max = 255) String title, @Size(max = 5000) String description, @NotNull IssueType type, @NotNull IssuePriority priority,
                                     UUID assigneeId, UUID epicId, UUID sprintId, LocalDate dueDate, List<UUID> labelIds, @NotNull @Min(0) Long version) { }
    public record MoveIssueRequest(@NotNull UUID statusId, @Min(0) int position, @NotNull @Min(0) Long version) { }
    public record UserRef(UUID id, String displayName, String email) { }
    public record StatusRef(UUID id, String name, WorkflowCategory category, String color) { }
    public record EpicRef(UUID id, String name, String color) { }
    public record SprintRef(UUID id, String name, String state) { }
    public record LabelRef(UUID id, String name, String color) { }
    public record IssueResponse(UUID id, String issueKey, String title, String description, IssueType type, IssuePriority priority, StatusRef status,
                                UserRef reporter, UserRef assignee, EpicRef epic, SprintRef sprint, List<LabelRef> labels, LocalDate dueDate, int boardPosition,
                                long version, Instant createdAt, Instant updatedAt) { }
    public record IssuePageResponse(List<IssueResponse> content, int page, int size, long totalElements, int totalPages) { }
    public record BoardColumnResponse(UUID id, String name, WorkflowCategory category, String color, int displayOrder, List<IssueResponse> issues) { }
    public record BoardResponse(String projectKey, List<BoardColumnResponse> columns) { }
    public record MetricsResponse(long totalIssues, Map<String, Long> byStatus, Map<String, Long> byType, Map<String, Long> byPriority, Map<String, Long> byAssignee) { }
}

