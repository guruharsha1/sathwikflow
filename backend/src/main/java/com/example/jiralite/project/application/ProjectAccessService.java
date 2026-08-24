package com.example.jiralite.project.application;

import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.project.domain.Project;
import com.example.jiralite.project.domain.ProjectMember;
import com.example.jiralite.project.domain.ProjectRole;
import com.example.jiralite.project.persistence.ProjectMemberRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {
    private final ProjectMemberRepository members;
    public ProjectAccessService(ProjectMemberRepository members) { this.members = members; }
    public ProjectMember requireMember(Project project, UUID userId, boolean systemAdmin) {
        if (systemAdmin) return null;
        return members.findByIdProjectIdAndIdUserId(project.getId(), userId)
                .orElseThrow(() -> DomainException.forbidden("You are not a member of this project"));
    }
    public void requireEditor(Project project, UUID userId, boolean systemAdmin) {
        ProjectMember member = requireMember(project, userId, systemAdmin);
        if (member != null && member.getRole() == ProjectRole.VIEWER) throw DomainException.forbidden("Viewers cannot change project work");
    }
    public void requireAdmin(Project project, UUID userId, boolean systemAdmin) {
        ProjectMember member = requireMember(project, userId, systemAdmin);
        if (member != null && member.getRole() != ProjectRole.PROJECT_ADMIN) throw DomainException.forbidden("Project administrator permission is required");
    }
    public boolean canAdmin(Project project, UUID userId, boolean systemAdmin) {
        if (systemAdmin) return true;
        return members.findByIdProjectIdAndIdUserId(project.getId(), userId).map(m -> m.getRole() == ProjectRole.PROJECT_ADMIN).orElse(false);
    }
}

