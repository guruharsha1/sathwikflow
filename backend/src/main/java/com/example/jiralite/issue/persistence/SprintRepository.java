package com.example.jiralite.issue.persistence;
import com.example.jiralite.issue.domain.Sprint;
import com.example.jiralite.issue.domain.SprintState;
import java.util.List; import java.util.Optional; import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SprintRepository extends JpaRepository<Sprint, UUID> { List<Sprint> findByProjectIdOrderByCreatedAtDesc(UUID projectId); Optional<Sprint> findByIdAndProjectId(UUID id, UUID projectId); boolean existsByProjectIdAndState(UUID projectId, SprintState state); void deleteByProjectId(UUID projectId); }

