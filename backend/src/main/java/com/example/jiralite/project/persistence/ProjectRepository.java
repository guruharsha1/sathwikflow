package com.example.jiralite.project.persistence;

import com.example.jiralite.project.domain.Project;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByProjectKey(String projectKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Project> findWithLockByProjectKey(String projectKey);
}
