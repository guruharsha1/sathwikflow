package com.example.jiralite.notification.application;

import com.example.jiralite.notification.domain.NotificationOutbox;
import com.example.jiralite.notification.persistence.NotificationOutboxRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationWorker {
    private final NotificationOutboxRepository outbox; private final JavaMailSender mail;
    public NotificationWorker(NotificationOutboxRepository outbox, JavaMailSender mail) { this.outbox = outbox; this.mail = mail; }
    @Scheduled(fixedDelayString = "${sathwikflow.notifications.poll-delay-ms:30000}")
    @Transactional
    public void deliverDueNotifications() {
        for (NotificationOutbox entry : outbox.findReady(Instant.now(), PageRequest.of(0, 50))) {
            try { SimpleMailMessage message = new SimpleMailMessage(); message.setTo(entry.getRecipientEmail()); message.setSubject(entry.getSubject()); message.setText(entry.getBodyText()); mail.send(message); entry.sent(); }
            catch (Exception exception) { entry.failed(exception); }
        }
    }
}

