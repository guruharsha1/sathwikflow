package com.example.jiralite.workflow;

import com.example.jiralite.common.security.CurrentUser;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/projects/{projectKey}/statuses")
public class WorkflowController {
    private final WorkflowService service; private final CurrentUser current;
    public WorkflowController(WorkflowService service, CurrentUser current) { this.service = service; this.current = current; }
    @GetMapping public List<WorkflowDtos.StatusResponse> list(@PathVariable String projectKey, Authentication auth) { return service.list(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping public ResponseEntity<WorkflowDtos.StatusResponse> create(@PathVariable String projectKey, @Valid @RequestBody WorkflowDtos.StatusRequest body, Authentication auth) {
        WorkflowDtos.StatusResponse status = service.create(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + projectKey + "/statuses/" + status.id())).body(status);
    }
    @PatchMapping("/{statusId}") public WorkflowDtos.StatusResponse update(@PathVariable String projectKey, @PathVariable UUID statusId, @Valid @RequestBody WorkflowDtos.StatusRequest body, Authentication auth) { return service.update(projectKey, statusId, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/{statusId}") public ResponseEntity<Void> delete(@PathVariable String projectKey, @PathVariable UUID statusId, Authentication auth) { service.delete(projectKey, statusId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
}

