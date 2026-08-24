package com.example.jiralite.issue.persistence;
import com.example.jiralite.issue.domain.Label;
import java.util.Collection; import java.util.List; import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LabelRepository extends JpaRepository<Label, UUID> { List<Label> findByProjectIdOrderByNameAsc(UUID projectId); List<Label> findByProjectIdAndIdIn(UUID projectId, Collection<UUID> ids); void deleteByProjectId(UUID projectId); }

