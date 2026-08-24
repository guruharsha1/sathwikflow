package com.example.jiralite.notification.application;

import com.example.jiralite.issue.domain.Issue;
import com.example.jiralite.notification.domain.NotificationOutbox;
import com.example.jiralite.notification.persistence.NotificationOutboxRepository;
import com.example.jiralite.user.UserAccount;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationOutboxRepository outbox;
    public NotificationService(NotificationOutboxRepository outbox) { this.outbox = outbox; }
    public void assigned(Issue issue, UserAccount recipient, UserAccount actor) {
        if (recipient == null || recipient.getId().equals(actor.getId())) return;
        queue("ISSUE_ASSIGNED", recipient, "You were assigned " + issue.getIssueKey(), actor.getDisplayName() + " assigned you to “" + issue.getTitle() + "”.", issue);
    }
    public void commented(Issue issue, UserAccount actor) {
        Set<UserAccount> recipients = new HashSet<>(); recipients.add(issue.getReporter()); if (issue.getAssignee() != null) recipients.add(issue.getAssignee());
        recipients.stream().filter(user -> !user.getId().equals(actor.getId())).forEach(user -> queue("ISSUE_COMMENTED", user,
                "New comment on " + issue.getIssueKey(), actor.getDisplayName() + " commented on “" + issue.getTitle() + "”.", issue));
    }
    private void queue(String type, UserAccount recipient, String subject, String body, Issue issue) {
        outbox.save(new NotificationOutbox(type, recipient.getEmail(), subject, body, issue.getIssueKey(), "/issues/" + issue.getIssueKey()));
    }
}

