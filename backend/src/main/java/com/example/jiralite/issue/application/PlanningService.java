package com.example.jiralite.issue.application;

import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.issue.api.PlanningDtos;
import com.example.jiralite.issue.domain.Epic;
import com.example.jiralite.issue.domain.Label;
import com.example.jiralite.issue.domain.Sprint;
import com.example.jiralite.issue.domain.SprintState;
import com.example.jiralite.issue.persistence.EpicRepository;
import com.example.jiralite.issue.persistence.LabelRepository;
import com.example.jiralite.issue.persistence.SprintRepository;
import com.example.jiralite.project.application.ProjectAccessService;
import com.example.jiralite.project.application.ProjectService;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.project.persistence.ProjectRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlanningService {
    private final ProjectService projects; private final ProjectRepository projectRepository; private final ProjectAccessService access;
    private final EpicRepository epics; private final SprintRepository sprints; private final LabelRepository labels;
    public PlanningService(ProjectService projects, ProjectRepository projectRepository, ProjectAccessService access, EpicRepository epics, SprintRepository sprints, LabelRepository labels) {
        this.projects = projects; this.projectRepository = projectRepository; this.access = access; this.epics = epics; this.sprints = sprints; this.labels = labels;
    }
    @Transactional public List<PlanningDtos.EpicResponse> epics(String key, UUID actor, boolean admin) { Project p = projects.project(key); access.requireMember(p, actor, admin); return epics.findByProjectIdOrderByNameAsc(p.getId()).stream().map(this::epic).toList(); }
    @Transactional public PlanningDtos.EpicResponse createEpic(String key, PlanningDtos.EpicRequest request, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); return epic(epics.save(new Epic(p, request.name(), request.description(), request.color()))); }
    @Transactional public PlanningDtos.EpicResponse updateEpic(String key, UUID id, PlanningDtos.EpicRequest request, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); Epic epic = epics.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Epic not found")); epic.update(request.name(), request.description(), request.color()); return epic(epic); }
    @Transactional public void deleteEpic(String key, UUID id, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); Epic epic = epics.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Epic not found")); epics.delete(epic); }
    @Transactional public List<PlanningDtos.SprintResponse> sprints(String key, UUID actor, boolean admin) { Project p = projects.project(key); access.requireMember(p, actor, admin); return sprints.findByProjectIdOrderByCreatedAtDesc(p.getId()).stream().map(this::sprint).toList(); }
    @Transactional public PlanningDtos.SprintResponse createSprint(String key, PlanningDtos.SprintRequest request, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); return sprint(sprints.save(new Sprint(p, request.name(), request.goal(), request.startDate(), request.endDate()))); }
    @Transactional public PlanningDtos.SprintResponse updateSprint(String key, UUID id, PlanningDtos.SprintRequest request, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); Sprint sprint = sprints.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Sprint not found")); sprint.update(request.name(), request.goal(), request.startDate(), request.endDate()); return sprint(sprint); }
    @Transactional public void deleteSprint(String key, UUID id, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); Sprint sprint = sprints.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Sprint not found")); if (sprint.getState() == SprintState.ACTIVE) throw DomainException.validation("Complete the active sprint before deleting it"); sprints.delete(sprint); }
    @Transactional public PlanningDtos.SprintResponse startSprint(String key, UUID id, UUID actor, boolean admin) {
        Project p = projectRepository.findByProjectKeyForUpdate(key).orElseThrow(() -> DomainException.notFound("Project not found")); access.requireEditor(p, actor, admin);
        if (sprints.existsByProjectIdAndState(p.getId(), SprintState.ACTIVE)) throw DomainException.conflict("This project already has an active sprint");
        Sprint sprint = sprints.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Sprint not found")); sprint.start(); return sprint(sprint);
    }
    @Transactional public PlanningDtos.SprintResponse completeSprint(String key, UUID id, UUID actor, boolean admin) {
        Project p = projectRepository.findByProjectKeyForUpdate(key).orElseThrow(() -> DomainException.notFound("Project not found")); access.requireEditor(p, actor, admin);
        Sprint sprint = sprints.findByIdAndProjectId(id, p.getId()).orElseThrow(() -> DomainException.notFound("Sprint not found")); sprint.complete(); return sprint(sprint);
    }
    @Transactional public List<PlanningDtos.LabelResponse> labels(String key, UUID actor, boolean admin) { Project p = projects.project(key); access.requireMember(p, actor, admin); return labels.findByProjectIdOrderByNameAsc(p.getId()).stream().map(this::label).toList(); }
    @Transactional public PlanningDtos.LabelResponse createLabel(String key, PlanningDtos.LabelRequest request, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); return label(labels.save(new Label(p, request.name(), request.color()))); }
    @Transactional public void deleteLabel(String key, UUID id, UUID actor, boolean admin) { Project p = projects.project(key); access.requireEditor(p, actor, admin); Label label = labels.findById(id).filter(value -> value.getProject().getId().equals(p.getId())).orElseThrow(() -> DomainException.notFound("Label not found")); labels.delete(label); }
    private PlanningDtos.EpicResponse epic(Epic value) { return new PlanningDtos.EpicResponse(value.getId(), value.getName(), value.getDescription(), value.getColor(), value.getCreatedAt(), value.getUpdatedAt()); }
    private PlanningDtos.SprintResponse sprint(Sprint value) { return new PlanningDtos.SprintResponse(value.getId(), value.getName(), value.getGoal(), value.getState(), value.getStartDate(), value.getEndDate(), value.getCreatedAt(), value.getUpdatedAt()); }
    private PlanningDtos.LabelResponse label(Label value) { return new PlanningDtos.LabelResponse(value.getId(), value.getName(), value.getColor()); }
}

