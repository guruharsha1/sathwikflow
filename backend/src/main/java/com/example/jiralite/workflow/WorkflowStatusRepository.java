package com.example.jiralite.workflow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, UUID> {
    List<WorkflowStatus> findByProjectIdOrderByDisplayOrderAsc(UUID projectId);
    Optional<WorkflowStatus> findByIdAndProjectId(UUID id, UUID projectId);
    long countByProjectId(UUID projectId);
    void deleteByProjectId(UUID projectId);
}

