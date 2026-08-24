package com.example.jiralite.issue.persistence;
import com.example.jiralite.issue.domain.Epic;
import java.util.List; import java.util.Optional; import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EpicRepository extends JpaRepository<Epic, UUID> { List<Epic> findByProjectIdOrderByNameAsc(UUID projectId); Optional<Epic> findByIdAndProjectId(UUID id, UUID projectId); void deleteByProjectId(UUID projectId); }

