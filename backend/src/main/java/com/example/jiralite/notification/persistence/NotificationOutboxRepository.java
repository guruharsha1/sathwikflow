package com.example.jiralite.notification.persistence;
import com.example.jiralite.notification.domain.NotificationOutbox;
import java.time.Instant; import java.util.List; import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, UUID> {
    @Query("select n from NotificationOutbox n where n.status in ('PENDING', 'FAILED') and (n.nextAttemptAt is null or n.nextAttemptAt <= :now) order by n.createdAt asc")
    List<NotificationOutbox> findReady(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);
}

