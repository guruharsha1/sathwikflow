package com.example.jiralite.issue.api;

import com.example.jiralite.issue.domain.IssuePriority;
import com.example.jiralite.issue.domain.IssueType;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class IssueController {
    @GetMapping("/projects/{projectKey}/issues")
    List<IssueResponse> list(@PathVariable String projectKey) {
        return List.of();
    }

    public record CreateIssueRequest(@NotBlank String title, String description, IssueType type, IssuePriority priority) {
    }

    public record IssueResponse(String issueKey, String title, IssueType type, IssuePriority priority, String status) {
    }
}
