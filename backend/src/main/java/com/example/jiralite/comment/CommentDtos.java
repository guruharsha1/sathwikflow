package com.example.jiralite.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class CommentDtos {
    private CommentDtos() { }
    public record CommentRequest(@NotBlank @Size(max = 5000) String body) { }
    public record CommentResponse(UUID id, String body, AuthorResponse author, Instant createdAt, Instant updatedAt) { }
    public record AuthorResponse(UUID id, String displayName, String email) { }
    public record ActivityResponse(UUID id, String eventType, String fieldName, String oldValue, String newValue, AuthorResponse actor, Instant occurredAt) { }
}

