package com.example.jiralite.auth.persistence;

import com.example.jiralite.auth.domain.RefreshSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    Optional<RefreshSession> findByTokenHash(String tokenHash);
    @Modifying @Query("update RefreshSession s set s.revokedAt = CURRENT_TIMESTAMP where s.familyId = :family and s.revokedAt is null")
    int revokeFamily(@Param("family") UUID familyId);
}

