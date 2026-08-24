package com.example.jiralite.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    @Query("select u from UserAccount u where u.enabled = true and not exists (select m from ProjectMember m where m.id.projectId = :projectId and m.id.userId = u.id) and (lower(u.email) like lower(concat('%', :search, '%')) or lower(u.displayName) like lower(concat('%', :search, '%'))) order by u.displayName")
    List<UserAccount> findMemberCandidates(@Param("projectId") UUID projectId, @Param("search") String search, org.springframework.data.domain.Pageable page);
}
