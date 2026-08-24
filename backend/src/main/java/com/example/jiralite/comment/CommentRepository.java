package com.example.jiralite.comment;
import java.util.List; import java.util.Optional; import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @EntityGraph(attributePaths = {"author"}) List<Comment> findByIssueIdOrderByCreatedAtAsc(UUID issueId);
    @EntityGraph(attributePaths = {"issue", "issue.project", "author"}) @Query("select c from Comment c where c.id = :id") Optional<Comment> findDetailedById(@Param("id") UUID id);
    void deleteByIssueId(UUID issueId);
}
