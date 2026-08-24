package com.example.jiralite.project.persistence;

import com.example.jiralite.project.domain.Project;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByProjectKeyIgnoreCase(String projectKey);
    boolean existsByProjectKeyIgnoreCase(String projectKey);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Project p where lower(p.projectKey) = lower(:key)")
    Optional<Project> findByProjectKeyForUpdate(@Param("key") String key);
}

