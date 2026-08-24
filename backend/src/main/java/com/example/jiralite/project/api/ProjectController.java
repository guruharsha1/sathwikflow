package com.example.jiralite.project.api;

import com.example.jiralite.common.security.CurrentUser;
import com.example.jiralite.project.application.ProjectService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService service; private final CurrentUser current;
    public ProjectController(ProjectService service, CurrentUser current) { this.service = service; this.current = current; }
    @GetMapping public List<ProjectDtos.ProjectResponse> list(Authentication auth) { return service.list(current.id(auth)); }
    @PostMapping public ResponseEntity<ProjectDtos.ProjectResponse> create(@Valid @RequestBody ProjectDtos.CreateProjectRequest body, Authentication auth) {
        ProjectDtos.ProjectResponse project = service.create(body, current.id(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + project.projectKey())).body(project);
    }
    @GetMapping("/{projectKey}") public ProjectDtos.ProjectResponse get(@PathVariable String projectKey, Authentication auth) { return service.get(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PutMapping("/{projectKey}") public ProjectDtos.ProjectResponse update(@PathVariable String projectKey, @Valid @RequestBody ProjectDtos.UpdateProjectRequest body, Authentication auth) { return service.update(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/{projectKey}") public ResponseEntity<Void> delete(@PathVariable String projectKey, Authentication auth) { service.delete(projectKey, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
    @GetMapping("/{projectKey}/members") public List<ProjectDtos.MemberResponse> members(@PathVariable String projectKey, Authentication auth) { return service.members(projectKey, current.id(auth), current.isSystemAdmin(auth)); }
    @GetMapping("/{projectKey}/member-candidates") public List<ProjectDtos.MemberCandidateResponse> candidates(@PathVariable String projectKey, @RequestParam(defaultValue = "") String search, Authentication auth) { return service.candidates(projectKey, search, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/{projectKey}/members") public ResponseEntity<ProjectDtos.MemberResponse> addMember(@PathVariable String projectKey, @Valid @RequestBody ProjectDtos.AddMemberRequest body, Authentication auth) {
        ProjectDtos.MemberResponse member = service.addMember(projectKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/projects/" + projectKey + "/members/" + member.userId())).body(member);
    }
    @PatchMapping("/{projectKey}/members/{userId}") public ProjectDtos.MemberResponse changeRole(@PathVariable String projectKey, @PathVariable UUID userId, @Valid @RequestBody ProjectDtos.ChangeMemberRoleRequest body, Authentication auth) { return service.changeMemberRole(projectKey, userId, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/{projectKey}/members/{userId}") public ResponseEntity<Void> removeMember(@PathVariable String projectKey, @PathVariable UUID userId, Authentication auth) { service.removeMember(projectKey, userId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
}

