package com.example.jiralite.issue.api;

import com.example.jiralite.common.security.CurrentUser;
import com.example.jiralite.issue.application.PlanningService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/projects/{projectKey}")
public class PlanningController {
    private final PlanningService service; private final CurrentUser current;
    public PlanningController(PlanningService service, CurrentUser current) { this.service = service; this.current = current; }
    @GetMapping("/epics") public List<PlanningDtos.EpicResponse> epics(@PathVariable String projectKey, Authentication auth) { return service.epics(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/epics") public ResponseEntity<PlanningDtos.EpicResponse> createEpic(@PathVariable String projectKey, @Valid @RequestBody PlanningDtos.EpicRequest body, Authentication auth) { PlanningDtos.EpicResponse result = service.createEpic(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + projectKey + "/epics/" + result.id())).body(result); }
    @PutMapping("/epics/{epicId}") public PlanningDtos.EpicResponse updateEpic(@PathVariable String projectKey, @PathVariable UUID epicId, @Valid @RequestBody PlanningDtos.EpicRequest body, Authentication auth) { return service.updateEpic(projectKey, epicId, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/epics/{epicId}") public ResponseEntity<Void> deleteEpic(@PathVariable String projectKey, @PathVariable UUID epicId, Authentication auth) { service.deleteEpic(projectKey, epicId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
    @GetMapping("/sprints") public List<PlanningDtos.SprintResponse> sprints(@PathVariable String projectKey, Authentication auth) { return service.sprints(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/sprints") public ResponseEntity<PlanningDtos.SprintResponse> createSprint(@PathVariable String projectKey, @Valid @RequestBody PlanningDtos.SprintRequest body, Authentication auth) { PlanningDtos.SprintResponse result = service.createSprint(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + projectKey + "/sprints/" + result.id())).body(result); }
    @PutMapping("/sprints/{sprintId}") public PlanningDtos.SprintResponse updateSprint(@PathVariable String projectKey, @PathVariable UUID sprintId, @Valid @RequestBody PlanningDtos.SprintRequest body, Authentication auth) { return service.updateSprint(projectKey, sprintId, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/sprints/{sprintId}") public ResponseEntity<Void> deleteSprint(@PathVariable String projectKey, @PathVariable UUID sprintId, Authentication auth) { service.deleteSprint(projectKey, sprintId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
    @PostMapping("/sprints/{sprintId}/start") public PlanningDtos.SprintResponse start(@PathVariable String projectKey, @PathVariable UUID sprintId, Authentication auth) { return service.startSprint(projectKey, sprintId, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/sprints/{sprintId}/complete") public PlanningDtos.SprintResponse complete(@PathVariable String projectKey, @PathVariable UUID sprintId, Authentication auth) { return service.completeSprint(projectKey, sprintId, current.id(auth), current.isSystemAdmin(auth)); }
    @GetMapping("/labels") public List<PlanningDtos.LabelResponse> labels(@PathVariable String projectKey, Authentication auth) { return service.labels(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/labels") public ResponseEntity<PlanningDtos.LabelResponse> createLabel(@PathVariable String projectKey, @Valid @RequestBody PlanningDtos.LabelRequest body, Authentication auth) { PlanningDtos.LabelResponse result = service.createLabel(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + projectKey + "/labels/" + result.id())).body(result); }
    @DeleteMapping("/labels/{labelId}") public ResponseEntity<Void> deleteLabel(@PathVariable String projectKey, @PathVariable UUID labelId, Authentication auth) { service.deleteLabel(projectKey, labelId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
}

