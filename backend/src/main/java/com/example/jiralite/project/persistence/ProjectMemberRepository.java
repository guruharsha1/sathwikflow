package com.example.jiralite.project.persistence;

import com.example.jiralite.project.domain.ProjectMember;
import com.example.jiralite.project.domain.ProjectMemberId;
import com.example.jiralite.project.domain.ProjectRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    Optional<ProjectMember> findByIdProjectIdAndIdUserId(UUID projectId, UUID userId);
    List<ProjectMember> findByIdUserIdOrderByProjectNameAsc(UUID userId);
    List<ProjectMember> findByIdProjectIdOrderByJoinedAtAsc(UUID projectId);
    long countByIdProjectIdAndRole(UUID projectId, ProjectRole role);
    @Query("select m.id.userId from ProjectMember m where m.id.projectId = :projectId")
    List<UUID> findUserIdsByProjectId(UUID projectId);
    void deleteByIdProjectId(UUID projectId);
}

