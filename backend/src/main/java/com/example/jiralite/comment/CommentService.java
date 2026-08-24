package com.example.jiralite.comment;

import com.example.jiralite.activity.domain.ActivityEvent;
import com.example.jiralite.activity.persistence.ActivityEventRepository;
import com.example.jiralite.common.exception.DomainException;
import com.example.jiralite.issue.application.IssueService;
import com.example.jiralite.issue.domain.Issue;
import com.example.jiralite.notification.application.NotificationService;
import com.example.jiralite.project.application.ProjectAccessService;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository comments; private final IssueService issueService; private final ProjectAccessService access; private final UserRepository users;
    private final ActivityEventRepository activity; private final NotificationService notifications;
    public CommentService(CommentRepository comments, IssueService issueService, ProjectAccessService access, UserRepository users, ActivityEventRepository activity, NotificationService notifications) {
        this.comments = comments; this.issueService = issueService; this.access = access; this.users = users; this.activity = activity; this.notifications = notifications;
    }
    @Transactional public List<CommentDtos.CommentResponse> list(String issueKey, UUID actor, boolean sysAdmin) { Issue issue = issueService.issue(issueKey); access.requireMember(issue.getProject(), actor, sysAdmin); return comments.findByIssueIdOrderByCreatedAtAsc(issue.getId()).stream().map(this::response).toList(); }
    @Transactional public CommentDtos.CommentResponse create(String issueKey, CommentDtos.CommentRequest request, UUID actorId, boolean sysAdmin) {
        Issue issue = issueService.issue(issueKey); access.requireEditor(issue.getProject(), actorId, sysAdmin); UserAccount actor = user(actorId); Comment comment = comments.save(new Comment(issue, actor, request.body()));
        activity.save(new ActivityEvent(issue, actor, "COMMENT_ADDED", null, null, request.body())); notifications.commented(issue, actor); return response(comment);
    }
    @Transactional public CommentDtos.CommentResponse update(UUID id, CommentDtos.CommentRequest request, UUID actorId, boolean sysAdmin) {
        Comment comment = comment(id); access.requireEditor(comment.getIssue().getProject(), actorId, sysAdmin); verifyOwnerOrAdmin(comment, actorId, sysAdmin); String old = comment.getBody(); comment.update(request.body());
        activity.save(new ActivityEvent(comment.getIssue(), user(actorId), "COMMENT_UPDATED", "comment", old, comment.getBody())); return response(comment);
    }
    @Transactional public void delete(UUID id, UUID actorId, boolean sysAdmin) {
        Comment comment = comment(id); access.requireEditor(comment.getIssue().getProject(), actorId, sysAdmin); verifyOwnerOrAdmin(comment, actorId, sysAdmin);
        activity.save(new ActivityEvent(comment.getIssue(), user(actorId), "COMMENT_DELETED", "comment", comment.getBody(), null)); comments.delete(comment);
    }
    @Transactional public List<CommentDtos.ActivityResponse> activity(String issueKey, UUID actor, boolean sysAdmin) {
        Issue issue = issueService.issue(issueKey); access.requireMember(issue.getProject(), actor, sysAdmin); return activity.findByIssueIdOrderByOccurredAtDesc(issue.getId()).stream().map(event -> new CommentDtos.ActivityResponse(event.getId(), event.getEventType(), event.getFieldName(), event.getOldValue(), event.getNewValue(), author(event.getActor()), event.getOccurredAt())).toList();
    }
    private Comment comment(UUID id) { return comments.findDetailedById(id).orElseThrow(() -> DomainException.notFound("Comment not found")); }
    private void verifyOwnerOrAdmin(Comment comment, UUID actor, boolean sysAdmin) { if (!comment.getAuthor().getId().equals(actor) && !access.canAdmin(comment.getIssue().getProject(), actor, sysAdmin)) throw DomainException.forbidden("Only the comment author or a project administrator may change this comment"); }
    private UserAccount user(UUID id) { return users.findById(id).orElseThrow(() -> DomainException.unauthorized("Account not found")); }
    private CommentDtos.CommentResponse response(Comment comment) { return new CommentDtos.CommentResponse(comment.getId(), comment.getBody(), author(comment.getAuthor()), comment.getCreatedAt(), comment.getUpdatedAt()); }
    private CommentDtos.AuthorResponse author(UserAccount user) { return new CommentDtos.AuthorResponse(user.getId(), user.getDisplayName(), user.getEmail()); }
}
