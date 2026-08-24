package com.example.jiralite.issue.persistence;

import com.example.jiralite.issue.domain.Issue;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssueRepository extends JpaRepository<Issue, UUID>, JpaSpecificationExecutor<Issue> {
    @EntityGraph(attributePaths = {"project", "status", "reporter", "assignee", "epic", "sprint", "labels"})
    Optional<Issue> findByProjectIdAndIssueNumber(UUID projectId, long issueNumber);
    @EntityGraph(attributePaths = {"project", "status", "reporter", "assignee", "epic", "sprint", "labels"})
    List<Issue> findByProjectIdOrderByStatusDisplayOrderAscBoardPositionAsc(UUID projectId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Issue i where i.project.id = :projectId and i.status.id in :statusIds order by i.status.displayOrder asc, i.boardPosition asc")
    List<Issue> lockBoardIssues(@Param("projectId") UUID projectId, @Param("statusIds") Collection<UUID> statusIds);
    long countByStatusId(UUID statusId);
    long countByProjectId(UUID projectId);
    void deleteByProjectId(UUID projectId);
}

