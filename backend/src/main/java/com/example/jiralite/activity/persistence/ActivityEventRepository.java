package com.example.jiralite.activity.persistence;
import com.example.jiralite.activity.domain.ActivityEvent;
import java.util.List; import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> { List<ActivityEvent> findByIssueIdOrderByOccurredAtDesc(UUID issueId); void deleteByIssueId(UUID issueId); }

