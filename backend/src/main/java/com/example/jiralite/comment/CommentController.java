package com.example.jiralite.comment;

import com.example.jiralite.common.security.CurrentUser;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1")
public class CommentController {
    private final CommentService service; private final CurrentUser current;
    public CommentController(CommentService service, CurrentUser current) { this.service = service; this.current = current; }
    @GetMapping("/issues/{issueKey}/comments") public List<CommentDtos.CommentResponse> list(@PathVariable String issueKey, Authentication auth) { return service.list(issueKey, current.id(auth), current.isSystemAdmin(auth)); }
    @PostMapping("/issues/{issueKey}/comments") public ResponseEntity<CommentDtos.CommentResponse> create(@PathVariable String issueKey, @Valid @RequestBody CommentDtos.CommentRequest body, Authentication auth) { CommentDtos.CommentResponse comment = service.create(issueKey, body, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.created(URI.create("/api/v1/comments/" + comment.id())).body(comment); }
    @PatchMapping("/comments/{commentId}") public CommentDtos.CommentResponse update(@PathVariable UUID commentId, @Valid @RequestBody CommentDtos.CommentRequest body, Authentication auth) { return service.update(commentId, body, current.id(auth), current.isSystemAdmin(auth)); }
    @DeleteMapping("/comments/{commentId}") public ResponseEntity<Void> delete(@PathVariable UUID commentId, Authentication auth) { service.delete(commentId, current.id(auth), current.isSystemAdmin(auth)); return ResponseEntity.noContent().build(); }
    @GetMapping("/issues/{issueKey}/activity") public List<CommentDtos.ActivityResponse> activity(@PathVariable String issueKey, Authentication auth) { return service.activity(issueKey, current.id(auth), current.isSystemAdmin(auth)); }
}

