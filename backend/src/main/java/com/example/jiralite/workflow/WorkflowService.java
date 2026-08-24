package com.example.jiralite.workflow;

import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.project.application.ProjectAccessService;
import com.example.jiralite.project.application.ProjectService;
import com.example.jiralite.project.domain.Project;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {
    private final WorkflowStatusRepository statuses; private final ProjectService projects; private final ProjectAccessService access;
    private final com.example.jiralite.issue.persistence.IssueRepository issueRepository;
    @Transactional
    public List<WorkflowDtos.StatusResponse> list(String key, UUID actor, boolean sysAdmin) {
        Project project = projects.project(key); access.requireMember(project, actor, sysAdmin); return map(statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId()));
    }
    @Transactional
    public WorkflowDtos.StatusResponse create(String key, WorkflowDtos.StatusRequest request, UUID actor, boolean sysAdmin) {
        Project project = projects.project(key); access.requireAdmin(project, actor, sysAdmin); ensureInitial(project, null, request.initial());
        WorkflowStatus created = statuses.save(new WorkflowStatus(project, request.name(), request.category(), 10_000, request.color(), request.initial(), request.terminal()));
        List<WorkflowStatus> order = new ArrayList<>(statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId())); order.remove(created); order.add(position(request.displayOrder(), order.size()), created);
        reorder(order, created, request); return toResponse(created);
    }
    @Transactional
    public WorkflowDtos.StatusResponse update(String key, UUID statusId, WorkflowDtos.StatusRequest request, UUID actor, boolean sysAdmin) {
        Project project = projects.project(key); access.requireAdmin(project, actor, sysAdmin);
        WorkflowStatus status = statuses.findByIdAndProjectId(statusId, project.getId()).orElseThrow(() -> DomainException.notFound("Workflow status not found"));
        ensureInitial(project, status, request.initial());
        List<WorkflowStatus> order = new ArrayList<>(statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId())); order.remove(status); order.add(position(request.displayOrder(), order.size()), status);
        reorder(order, status, request); return toResponse(status);
    }
    @Transactional
    public void delete(String key, UUID statusId, UUID actor, boolean sysAdmin) {
        Project project = projects.project(key); access.requireAdmin(project, actor, sysAdmin);
        WorkflowStatus status = statuses.findByIdAndProjectId(statusId, project.getId()).orElseThrow(() -> DomainException.notFound("Workflow status not found"));
        if (statuses.countByProjectId(project.getId()) <= 1) throw DomainException.validation("A project must have at least one workflow status");
        if (status.isInitial()) throw DomainException.validation("Assign a different initial status before deleting this status");
        if (issueCount(status) > 0) throw DomainException.validation("Move issues from this status before deleting it");
        statuses.delete(status);
    }
    private long issueCount(WorkflowStatus status) { return issueRepository.countByStatusId(status.getId()); }
    public WorkflowService(WorkflowStatusRepository statuses, ProjectService projects, ProjectAccessService access, com.example.jiralite.issue.persistence.IssueRepository issueRepository) {
        this.statuses = statuses; this.projects = projects; this.access = access; this.issueRepository = issueRepository;
    }
    private void ensureInitial(Project project, WorkflowStatus current, boolean requestedInitial) {
        if (!requestedInitial) return;
        boolean another = statuses.findByProjectIdOrderByDisplayOrderAsc(project.getId()).stream().anyMatch(s -> s.isInitial() && !s.getId().equals(current == null ? null : current.getId()));
        if (another) throw DomainException.validation("A project can have only one initial status");
    }
    private void reorder(List<WorkflowStatus> order, WorkflowStatus changed, WorkflowDtos.StatusRequest request) {
        for (int index = 0; index < order.size(); index++) { WorkflowStatus status = order.get(index); status.update(status.getName(), status.getCategory(), 10_000 + index, status.getColor(), status.isInitial(), status.isTerminal()); }
        statuses.flush();
        for (int index = 0; index < order.size(); index++) { WorkflowStatus status = order.get(index); if (status.equals(changed)) status.update(request.name(), request.category(), index, request.color(), request.initial(), request.terminal()); else status.update(status.getName(), status.getCategory(), index, status.getColor(), status.isInitial(), status.isTerminal()); }
    }
    private int position(Integer requested, int max) { return requested == null ? max : Math.max(0, Math.min(requested, max)); }
    private List<WorkflowDtos.StatusResponse> map(List<WorkflowStatus> values) { return values.stream().map(this::toResponse).toList(); }
    private WorkflowDtos.StatusResponse toResponse(WorkflowStatus status) { return new WorkflowDtos.StatusResponse(status.getId(), status.getName(), status.getCategory(), status.getDisplayOrder(), status.getColor(), status.isInitial(), status.isTerminal()); }
}
