package com.example.jiralite.issue.persistence;

import com.example.jiralite.issue.domain.Issue;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findByProjectProjectKeyOrderByBoardPosition(String projectKey);
}
