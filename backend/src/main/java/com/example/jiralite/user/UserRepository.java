package com.example.jiralite.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);

    List<UserAccount> findTop10ByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(String email, String displayName);
}
