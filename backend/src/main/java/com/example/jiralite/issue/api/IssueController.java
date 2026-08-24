package com.example.jiralite.issue.api;

import com.example.jiralite.comment.CommentRepository;
import com.example.jiralite.common.security.CurrentUser;
import com.example.jiralite.issue.application.IssueService;
import com.example.jiralite.issue.domain.IssuePriority;
import com.example.jiralite.issue.domain.IssueType;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1")
public class IssueController {
    private final IssueService service; private final CurrentUser current; private final CommentRepository comments;
    public IssueController(IssueService service, CurrentUser current, CommentRepository comments) { this.service = service; this.current = current; this.comments = comments; }
    @GetMapping("/projects/{projectKey}/issues") public IssueDtos.IssuePageResponse list(@PathVariable String projectKey, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "updatedAt") String sort, @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) UUID statusId, @RequestParam(required = false) UUID assigneeId, @RequestParam(required = false) IssueType type, @RequestParam(required = false) IssuePriority priority, @RequestParam(required = false) String search, Authentication auth) {
        return service.list(projectKey, page, size, sort, direction, statusId, assigneeId, type, priority, search, current.id(auth), current.isSystemAdmin(auth));
    }
    @PostMapping("/projects/{projectKey}/issues") public ResponseEntity<IssueDtos.IssueResponse> create(@PathVariable String projectKey, @Valid @RequestBody IssueDtos.CreateIssueRequest body, Authentication auth) { IssueDtos.IssueResponse issue = service.create(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/issues/" + issue.issueKey())).body(issue); }
    @GetMapping("/issues/{issueKey}") public IssueDtos.IssueResponse get(@PathVariable String issueKey, Authentication auth) { return service.get(issueKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PutMapping("/issues/{issueKey}") public IssueDtos.IssueResponse update(@PathVariable String issueKey, @Valid @RequestBody IssueDtos.UpdateIssueRequest body, Authentication auth) { return service.update(issueKey, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/issues/{issueKey}") public ResponseEntity<Void> delete(@PathVariable String issueKey, Authentication auth) { service.delete(issueKey, current.id(auth), current.isSystemAdmin(auth), comments); return ResponseEntity.noContent().build(); }
    @PatchMapping("/issues/{issueKey}/move") public IssueDtos.IssueResponse move(@PathVariable String issueKey, @Valid @RequestBody IssueDtos.MoveIssueRequest body, Authentication auth) { return service.move(issueKey, body, current.id(auth), current.isSystemAdmin(auth)); }
    @GetMapping("/projects/{projectKey}/board") public IssueDtos.BoardResponse board(@PathVariable String projectKey, Authentication auth) { return service.board(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @GetMapping("/projects/{projectKey}/metrics") public IssueDtos.MetricsResponse metrics(@PathVariable String projectKey, Authentication auth) { return service.metrics(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
}

