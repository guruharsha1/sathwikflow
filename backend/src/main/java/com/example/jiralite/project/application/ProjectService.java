package com.example.jiralite.project.application;

import com.example.jiralite.activity.persistence.ActivityEventRepository;
import com.example.jiralite.comment.CommentRepository;
import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.issue.persistence.EpicRepository;
import com.example.jiralite.issue.persistence.IssueRepository;
import com.example.jiralite.issue.persistence.LabelRepository;
import com.example.jiralite.issue.persistence.SprintRepository;
import com.example.jiralite.project.api.ProjectDtos;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.project.domain.ProjectMember;
import com.example.jiralite.project.domain.ProjectMemberId;
import com.example.jiralite.project.domain.ProjectRole;
import com.example.jiralite.project.persistence.ProjectMemberRepository;
import com.example.jiralite.project.persistence.ProjectRepository;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import com.example.jiralite.workflow.WorkflowCategory;
import com.example.jiralite.workflow.WorkflowStatus;
import com.example.jiralite.workflow.WorkflowStatusRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final ProjectRepository projects; private final ProjectMemberRepository members; private final UserRepository users; private final WorkflowStatusRepository statuses;
    private final ProjectAccessService access; private final IssueRepository issues; private final CommentRepository comments; private final ActivityEventRepository activity;
    private final EpicRepository epics; private final SprintRepository sprints; private final LabelRepository labels;
    public ProjectService(ProjectRepository projects, ProjectMemberRepository members, UserRepository users, WorkflowStatusRepository statuses, ProjectAccessService access,
                          IssueRepository issues, CommentRepository comments, ActivityEventRepository activity, EpicRepository epics, SprintRepository sprints, LabelRepository labels) {
        this.projects = projects; this.members = members; this.users = users; this.statuses = statuses; this.access = access; this.issues = issues; this.comments = comments; this.activity = activity; this.epics = epics; this.sprints = sprints; this.labels = labels;
    }
    @Transactional
    public ProjectDtos.ProjectResponse create(ProjectDtos.CreateProjectRequest request, UUID actorId) {
        String key = request.projectKey().trim().toUpperCase(Locale.ROOT);
        if (projects.existsByProjectKeyIgnoreCase(key)) throw DomainException.conflict("Project key already exists");
        UserAccount actor = users.findById(actorId).orElseThrow(() -> DomainException.unauthorized("Account not found"));
        Project project = projects.save(new Project(key, request.name(), request.description(), actor));
        members.save(new ProjectMember(project, actor, ProjectRole.PROJECT_ADMIN));
        statuses.saveAll(List.of(new WorkflowStatus(project, "Backlog", WorkflowCategory.BACKLOG, 0, "#64748b", true, false),
                new WorkflowStatus(project, "To Do", WorkflowCategory.TODO, 1, "#6366f1", false, false),
                new WorkflowStatus(project, "In Progress", WorkflowCategory.IN_PROGRESS, 2, "#f59e0b", false, false),
                new WorkflowStatus(project, "In Review", WorkflowCategory.IN_REVIEW, 3, "#8b5cf6", false, false),
                new WorkflowStatus(project, "Done", WorkflowCategory.DONE, 4, "#22c55e", false, true)));
        return toProject(project, ProjectRole.PROJECT_ADMIN);
    }
    @Transactional
    public List<ProjectDtos.ProjectResponse> list(UUID actorId) {
        return members.findByIdUserIdOrderByProjectNameAsc(actorId).stream().map(member -> toProject(member.getProject(), member.getRole())).toList();
    }
    @Transactional
    public ProjectDtos.ProjectResponse get(String key, UUID actorId, boolean sysAdmin) {
        Project project = project(key); ProjectMember member = access.requireMember(project, actorId, sysAdmin);
        return toProject(project, member == null ? ProjectRole.PROJECT_ADMIN : member.getRole());
    }
    @Transactional
    public ProjectDtos.ProjectResponse update(String key, ProjectDtos.UpdateProjectRequest request, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin); project.update(request.name(), request.description());
        ProjectMember member = access.requireMember(project, actorId, sysAdmin); return toProject(project, member == null ? ProjectRole.PROJECT_ADMIN : member.getRole());
    }
    @Transactional
    public void delete(String key, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin);
        var projectIssues = issues.findByProjectIdOrderByStatusDisplayOrderAscBoardPositionAsc(project.getId());
        projectIssues.forEach(issue -> { activity.deleteByIssueId(issue.getId()); comments.deleteByIssueId(issue.getId()); });
        issues.deleteAll(projectIssues); issues.flush(); epics.deleteByProjectId(project.getId()); sprints.deleteByProjectId(project.getId()); labels.deleteByProjectId(project.getId());
        statuses.deleteByProjectId(project.getId()); members.deleteByIdProjectId(project.getId()); projects.delete(project);
    }
    @Transactional
    public List<ProjectDtos.MemberResponse> members(String key, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireMember(project, actorId, sysAdmin); return members.findByIdProjectIdOrderByJoinedAtAsc(project.getId()).stream().map(this::toMember).toList();
    }
    @Transactional
    public List<ProjectDtos.MemberCandidateResponse> candidates(String key, String search, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin);
        String term = search == null ? "" : search.trim(); return users.findMemberCandidates(project.getId(), term, PageRequest.of(0, 20)).stream().map(u -> new ProjectDtos.MemberCandidateResponse(u.getId(), u.getDisplayName(), u.getEmail())).toList();
    }
    @Transactional
    public ProjectDtos.MemberResponse addMember(String key, ProjectDtos.AddMemberRequest request, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin);
        ProjectMemberId id = new ProjectMemberId(project.getId(), request.userId()); if (members.existsById(id)) throw DomainException.conflict("User is already a project member");
        UserAccount user = users.findById(request.userId()).filter(UserAccount::isEnabled).orElseThrow(() -> DomainException.notFound("User not found"));
        return toMember(members.save(new ProjectMember(project, user, request.role())));
    }
    @Transactional
    public ProjectDtos.MemberResponse changeMemberRole(String key, UUID userId, ProjectDtos.ChangeMemberRoleRequest request, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin);
        ProjectMember member = members.findByIdProjectIdAndIdUserId(project.getId(), userId).orElseThrow(() -> DomainException.notFound("Project member not found"));
        protectLastAdmin(project, member, request.role()); member.changeRole(request.role()); return toMember(member);
    }
    @Transactional
    public void removeMember(String key, UUID userId, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireAdmin(project, actorId, sysAdmin);
        ProjectMember member = members.findByIdProjectIdAndIdUserId(project.getId(), userId).orElseThrow(() -> DomainException.notFound("Project member not found"));
        protectLastAdmin(project, member, null); members.delete(member);
    }
    private void protectLastAdmin(Project project, ProjectMember member, ProjectRole nextRole) {
        if (member.getRole() == ProjectRole.PROJECT_ADMIN && nextRole != ProjectRole.PROJECT_ADMIN && members.countByIdProjectIdAndRole(project.getId(), ProjectRole.PROJECT_ADMIN) <= 1)
            throw DomainException.validation("A project must retain at least one administrator");
    }
    public Project project(String key) { return projects.findByProjectKeyIgnoreCase(key).orElseThrow(() -> DomainException.notFound("Project not found")); }
    private ProjectDtos.ProjectResponse toProject(Project project, ProjectRole role) { return new ProjectDtos.ProjectResponse(project.getId(), project.getProjectKey(), project.getName(), project.getDescription(), role, project.getCreatedAt(), project.getUpdatedAt()); }
    private ProjectDtos.MemberResponse toMember(ProjectMember member) { UserAccount user = member.getUser(); return new ProjectDtos.MemberResponse(user.getId(), user.getDisplayName(), user.getEmail(), member.getRole(), member.getJoinedAt()); }
}

