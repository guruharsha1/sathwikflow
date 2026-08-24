package com.example.jiralite.issue.application;

import com.example.jiralite.activity.domain.ActivityEvent;
import com.example.jiralite.activity.persistence.ActivityEventRepository;
import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.issue.api.IssueDtos;
import com.example.jiralite.issue.domain.Epic;
import com.example.jiralite.issue.domain.Issue;
import com.example.jiralite.issue.domain.IssuePriority;
import com.example.jiralite.issue.domain.IssueType;
import com.example.jiralite.issue.domain.Label;
import com.example.jiralite.issue.domain.Sprint;
import com.example.jiralite.issue.persistence.EpicRepository;
import com.example.jiralite.issue.persistence.IssueRepository;
import com.example.jiralite.issue.persistence.LabelRepository;
import com.example.jiralite.issue.persistence.SprintRepository;
import com.example.jiralite.notification.application.NotificationService;
import com.example.jiralite.project.application.ProjectAccessService;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.project.domain.ProjectMember;
import com.example.jiralite.project.persistence.ProjectMemberRepository;
import com.example.jiralite.project.persistence.ProjectRepository;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import com.example.jiralite.workflow.WorkflowStatus;
import com.example.jiralite.workflow.WorkflowStatusRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class IssueService {
    private final ProjectRepository projects; private final ProjectMemberRepository members; private final ProjectAccessService access; private final UserRepository users;
    private final WorkflowStatusRepository statuses; private final IssueRepository issues; private final EpicRepository epics; private final SprintRepository sprints; private final LabelRepository labels;
    private final ActivityEventRepository activity; private final NotificationService notifications;
    public IssueService(ProjectRepository projects, ProjectMemberRepository members, ProjectAccessService access, UserRepository users, WorkflowStatusRepository statuses,
                        IssueRepository issues, EpicRepository epics, SprintRepository sprints, LabelRepository labels, ActivityEventRepository activity, NotificationService notifications) {
        this.projects = projects; this.members = members; this.access = access; this.users = users; this.statuses = statuses; this.issues = issues; this.epics = epics; this.sprints = sprints; this.labels = labels; this.activity = activity; this.notifications = notifications;
    }
    @Transactional
    public IssueDtos.IssueResponse create(String key, IssueDtos.CreateIssueRequest request, UUID actorId, boolean sysAdmin) {
        Project project = lockProject(key); access.requireEditor(project, actorId, sysAdmin); UserAccount actor = user(actorId);
        WorkflowStatus status = status(project, request.statusId()); UserAccount assignee = assignee(project, request.assigneeId());
        Set<Label> resolvedLabels = labels(project, request.labelIds()); Epic epic = epic(project, request.epicId()); Sprint sprint = sprint(project, request.sprintId());
        int position = (int) issues.findByProjectIdOrderByStatusDisplayOrderAscBoardPositionAsc(project.getId()).stream().filter(i -> i.getStatus().getId().equals(status.getId())).count();
        Issue issue = issues.save(new Issue(project, project.allocateIssueNumber(), request.title(), request.description(), request.type(), request.priority(), status, actor, assignee, epic, sprint, request.dueDate(), position, resolvedLabels));
        activity.save(new ActivityEvent(issue, actor, "ISSUE_CREATED", null, null, issue.getIssueKey()));
        if (assignee != null) notifications.assigned(issue, assignee, actor);
        issues.flush(); return toResponse(issue);
    }
    @Transactional
    public IssueDtos.IssueResponse get(String issueKey, UUID actorId, boolean sysAdmin) {
        Issue issue = issue(issueKey); access.requireMember(issue.getProject(), actorId, sysAdmin); return toResponse(issue);
    }
    @Transactional
    public IssueDtos.IssuePageResponse list(String key, int page, int size, String sort, String direction, UUID statusId, UUID assigneeId, IssueType type, IssuePriority priority, String search, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireMember(project, actorId, sysAdmin);
        int safePage = Math.max(page, 0); int safeSize = Math.min(Math.max(size, 1), 100); String property = allowedSort(sort);
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Specification<Issue> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(); predicates.add(cb.equal(root.get("project").get("id"), project.getId()));
            if (statusId != null) predicates.add(cb.equal(root.get("status").get("id"), statusId)); if (assigneeId != null) predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
            if (type != null) predicates.add(cb.equal(root.get("type"), type)); if (priority != null) predicates.add(cb.equal(root.get("priority"), priority));
            if (search != null && !search.isBlank()) { String term = "%" + search.trim().toLowerCase() + "%"; predicates.add(cb.or(cb.like(cb.lower(root.get("title")), term), cb.like(cb.lower(cb.coalesce(root.get("description"), "")), term))); }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        Page<Issue> result = issues.findAll(specification, PageRequest.of(safePage, safeSize, Sort.by(sortDirection, property)));
        return new IssueDtos.IssuePageResponse(result.getContent().stream().map(this::toResponse).toList(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
    @Transactional
    public IssueDtos.IssueResponse update(String issueKey, IssueDtos.UpdateIssueRequest request, UUID actorId, boolean sysAdmin) {
        Issue issue = issue(issueKey); access.requireEditor(issue.getProject(), actorId, sysAdmin); ensureVersion(issue, request.version()); UserAccount actor = user(actorId);
        String oldTitle = issue.getTitle(); String oldDescription = issue.getDescription(); IssueType oldType = issue.getType(); IssuePriority oldPriority = issue.getPriority();
        UserAccount oldAssignee = issue.getAssignee(); Epic oldEpic = issue.getEpic(); Sprint oldSprint = issue.getSprint(); LocalDate oldDue = issue.getDueDate(); Set<Label> oldLabels = issue.getLabels();
        UserAccount nextAssignee = assignee(issue.getProject(), request.assigneeId()); Epic nextEpic = epic(issue.getProject(), request.epicId()); Sprint nextSprint = sprint(issue.getProject(), request.sprintId()); Set<Label> nextLabels = labels(issue.getProject(), request.labelIds());
        issue.update(request.title(), request.description(), request.type(), request.priority(), nextAssignee, nextEpic, nextSprint, request.dueDate(), nextLabels);
        changed(issue, actor, "title", oldTitle, issue.getTitle()); changed(issue, actor, "description", oldDescription, issue.getDescription()); changed(issue, actor, "type", oldType, issue.getType()); changed(issue, actor, "priority", oldPriority, issue.getPriority());
        changed(issue, actor, "assignee", name(oldAssignee), name(nextAssignee)); changed(issue, actor, "epic", name(oldEpic), name(nextEpic)); changed(issue, actor, "sprint", name(oldSprint), name(nextSprint)); changed(issue, actor, "dueDate", oldDue, request.dueDate());
        if (!sameLabels(oldLabels, nextLabels)) activity.save(new ActivityEvent(issue, actor, "ISSUE_UPDATED", "labels", labelNames(oldLabels), labelNames(nextLabels)));
        if (!sameUser(oldAssignee, nextAssignee) && nextAssignee != null) notifications.assigned(issue, nextAssignee, actor);
        issues.flush(); return toResponse(issue);
    }
    @Transactional
    public IssueDtos.IssueResponse move(String issueKey, IssueDtos.MoveIssueRequest request, UUID actorId, boolean sysAdmin) {
        Issue loaded = issue(issueKey); Project project = lockProject(loaded.getProject().getProjectKey()); access.requireEditor(project, actorId, sysAdmin);
        Issue issue = issues.findByProjectIdAndIssueNumber(project.getId(), loaded.getIssueNumber()).orElseThrow(() -> DomainException.notFound("Issue not found")); ensureVersion(issue, request.version());
        WorkflowStatus target = statuses.findByIdAndProjectId(request.statusId(), project.getId()).orElseThrow(() -> DomainException.validation("Target status is not in this project"));
        List<WorkflowStatus> allStatuses = statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId());
        List<Issue> lockedBoard = issues.lockBoardIssues(project.getId(), allStatuses.stream().map(WorkflowStatus::getId).toList());
        Map<UUID, List<Issue>> columns = new LinkedHashMap<>(); allStatuses.forEach(status -> columns.put(status.getId(), new ArrayList<>())); lockedBoard.forEach(card -> columns.get(card.getStatus().getId()).add(card));
        Issue moved = lockedBoard.stream().filter(card -> card.getId().equals(issue.getId())).findFirst().orElse(issue); WorkflowStatus oldStatus = moved.getStatus();
        columns.get(oldStatus.getId()).removeIf(card -> card.getId().equals(moved.getId())); List<Issue> targetColumn = columns.get(target.getId()); targetColumn.add(Math.max(0, Math.min(request.position(), targetColumn.size())), moved);
        for (WorkflowStatus status : allStatuses) { List<Issue> cards = columns.get(status.getId()); for (int index = 0; index < cards.size(); index++) { Issue card = cards.get(index); if (card.getId().equals(moved.getId())) card.moveTo(target, index); else card.setBoardPosition(index); } }
        UserAccount actor = user(actorId); activity.save(new ActivityEvent(moved, actor, "ISSUE_MOVED", "status", oldStatus.getName(), target.getName()));
        issues.flush(); return toResponse(moved);
    }
    @Transactional
    public void delete(String issueKey, UUID actorId, boolean sysAdmin, com.example.jiralite.comment.CommentRepository comments) {
        Issue issue = issue(issueKey); access.requireEditor(issue.getProject(), actorId, sysAdmin); activity.deleteByIssueId(issue.getId()); comments.deleteByIssueId(issue.getId()); issues.delete(issue); issues.flush();
    }
    @Transactional
    public IssueDtos.BoardResponse board(String key, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireMember(project, actorId, sysAdmin); List<WorkflowStatus> allStatuses = statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId());
        Map<UUID, List<IssueDtos.IssueResponse>> cards = new LinkedHashMap<>(); allStatuses.forEach(status -> cards.put(status.getId(), new ArrayList<>()));
        issues.findByProjectIdOrderByStatusDisplayOrderAscBoardPositionAsc(project.getId()).forEach(issue -> cards.get(issue.getStatus().getId()).add(toResponse(issue)));
        return new IssueDtos.BoardResponse(project.getProjectKey(), allStatuses.stream().map(status -> new IssueDtos.BoardColumnResponse(status.getId(), status.getName(), status.getCategory(), status.getColor(), status.getDisplayOrder(), cards.get(status.getId()))).toList());
    }
    @Transactional
    public IssueDtos.MetricsResponse metrics(String key, UUID actorId, boolean sysAdmin) {
        Project project = project(key); access.requireMember(project, actorId, sysAdmin); List<Issue> all = issues.findByProjectIdOrderByStatusDisplayOrderAscBoardPositionAsc(project.getId());
        Map<String, Long> byStatus = count(all, issue -> issue.getStatus().getName()); Map<String, Long> byType = count(all, issue -> issue.getType().name()); Map<String, Long> byPriority = count(all, issue -> issue.getPriority().name()); Map<String, Long> byAssignee = count(all, issue -> issue.getAssignee() == null ? "Unassigned" : issue.getAssignee().getDisplayName());
        return new IssueDtos.MetricsResponse(all.size(), byStatus, byType, byPriority, byAssignee);
    }
    public Issue issue(String issueKey) {
        int separator = issueKey.lastIndexOf('-'); if (separator <= 0 || separator == issueKey.length() - 1) throw DomainException.notFound("Issue not found");
        long number; try { number = Long.parseLong(issueKey.substring(separator + 1)); } catch (NumberFormatException ex) { throw DomainException.notFound("Issue not found"); }
        Project project = project(issueKey.substring(0, separator)); return issues.findByProjectIdAndIssueNumber(project.getId(), number).orElseThrow(() -> DomainException.notFound("Issue not found"));
    }
    private Project project(String key) { return projects.findByProjectKeyIgnoreCase(key).orElseThrow(() -> DomainException.notFound("Project not found")); }
    private Project lockProject(String key) { return projects.findByProjectKeyForUpdate(key).orElseThrow(() -> DomainException.notFound("Project not found")); }
    private UserAccount user(UUID id) { return users.findById(id).orElseThrow(() -> DomainException.unauthorized("Account not found")); }
    private WorkflowStatus status(Project project, UUID id) { if (id != null) return statuses.findByIdAndProjectId(id, project.getId()).orElseThrow(() -> DomainException.validation("Status is not in this project")); return statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId()).stream().filter(WorkflowStatus::isInitial).findFirst().orElseThrow(() -> DomainException.validation("Project has no initial status")); }
    private UserAccount assignee(Project project, UUID id) { if (id == null) return null; ProjectMember member = members.findByIdProjectIdAndIdUserId(project.getId(), id).orElseThrow(() -> DomainException.validation("Assignee must be a project member")); if (!member.getUser().isEnabled()) throw DomainException.validation("Assignee account is disabled"); return member.getUser(); }
    private Epic epic(Project project, UUID id) { return id == null ? null : epics.findByIdAndProjectId(id, project.getId()).orElseThrow(() -> DomainException.validation("Epic is not in this project")); }
    private Sprint sprint(Project project, UUID id) { return id == null ? null : sprints.findByIdAndProjectId(id, project.getId()).orElseThrow(() -> DomainException.validation("Sprint is not in this project")); }
    private Set<Label> labels(Project project, List<UUID> ids) { if (ids == null || ids.isEmpty()) return Set.of(); Set<UUID> unique = new LinkedHashSet<>(ids); List<Label> result = labels.findByProjectIdAndIdIn(project.getId(), unique); if (result.size() != unique.size()) throw DomainException.validation("One or more labels are not in this project"); return new LinkedHashSet<>(result); }
    private void ensureVersion(Issue issue, Long version) { if (!Objects.equals(issue.getVersion(), version)) throw DomainException.conflict("This issue was changed by someone else. Reload it and try again."); }
    private void changed(Issue issue, UserAccount actor, String field, Object oldValue, Object newValue) { if (!Objects.equals(String.valueOf(oldValue), String.valueOf(newValue))) activity.save(new ActivityEvent(issue, actor, "ISSUE_UPDATED", field, string(oldValue), string(newValue))); }
    private String string(Object value) { return value == null ? null : String.valueOf(value); } private String name(UserAccount user) { return user == null ? null : user.getDisplayName(); } private String name(Epic value) { return value == null ? null : value.getName(); } private String name(Sprint value) { return value == null ? null : value.getName(); }
    private boolean sameUser(UserAccount one, UserAccount other) { return one == null ? other == null : other != null && one.getId().equals(other.getId()); } private boolean sameLabels(Set<Label> one, Set<Label> other) { return one.stream().map(Label::getId).collect(java.util.stream.Collectors.toSet()).equals(other.stream().map(Label::getId).collect(java.util.stream.Collectors.toSet())); }
    private String labelNames(Set<Label> values) { return values.stream().map(Label::getName).sorted().reduce((a, b) -> a + ", " + b).orElse(""); }
    private String allowedSort(String requested) { return switch (requested == null ? "updatedAt" : requested) { case "createdAt", "updatedAt", "title", "priority", "issueNumber" -> requested == null ? "updatedAt" : requested; default -> "updatedAt"; }; }
    private Map<String, Long> count(Collection<Issue> all, java.util.function.Function<Issue, String> extractor) { Map<String, Long> totals = new LinkedHashMap<>(); all.forEach(issue -> totals.merge(extractor.apply(issue), 1L, Long::sum)); return totals; }
    private IssueDtos.IssueResponse toResponse(Issue issue) {
        var status = issue.getStatus(); return new IssueDtos.IssueResponse(issue.getId(), issue.getIssueKey(), issue.getTitle(), issue.getDescription(), issue.getType(), issue.getPriority(),
                new IssueDtos.StatusRef(status.getId(), status.getName(), status.getCategory(), status.getColor()), userRef(issue.getReporter()), userRef(issue.getAssignee()), epicRef(issue.getEpic()), sprintRef(issue.getSprint()),
                issue.getLabels().stream().sorted(Comparator.comparing(Label::getName)).map(label -> new IssueDtos.LabelRef(label.getId(), label.getName(), label.getColor())).toList(), issue.getDueDate(), issue.getBoardPosition(), issue.getVersion(), issue.getCreatedAt(), issue.getUpdatedAt());
    }
    private IssueDtos.UserRef userRef(UserAccount user) { return user == null ? null : new IssueDtos.UserRef(user.getId(), user.getDisplayName(), user.getEmail()); }
    private IssueDtos.EpicRef epicRef(Epic epic) { return epic == null ? null : new IssueDtos.EpicRef(epic.getId(), epic.getName(), epic.getColor()); }
    private IssueDtos.SprintRef sprintRef(Sprint sprint) { return sprint == null ? null : new IssueDtos.SprintRef(sprint.getId(), sprint.getName(), sprint.getState().name()); }
}

