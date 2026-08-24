package com.example.jiralite.common.config;

import com.example.jiralite.issue.api.IssueDtos;
import com.example.jiralite.issue.application.IssueService;
import com.example.jiralite.issue.domain.IssuePriority;
import com.example.jiralite.issue.domain.IssueType;
import com.example.jiralite.project.api.ProjectDtos;
import com.example.jiralite.project.application.ProjectService;
import com.example.jiralite.project.domain.ProjectRole;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataInitializer {
    @Bean @ConditionalOnProperty(prefix = "sathwikflow.demo", name = "seed", havingValue = "true")
    CommandLineRunner demoData(UserRepository users, PasswordEncoder passwords, ProjectService projects, IssueService issues, org.springframework.core.env.Environment environment) {
        return args -> {
            if (users.findByEmailIgnoreCase("admin@sathwikflow.dev").isPresent()) return;
            String password = environment.getProperty("sathwikflow.demo.password", "DemoPass123!");
            UserAccount admin = users.save(new UserAccount("Avery Admin", "admin@sathwikflow.dev", passwords.encode(password)));
            UserAccount member = users.save(new UserAccount("Mira Member", "member@sathwikflow.dev", passwords.encode(password)));
            UserAccount viewer = users.save(new UserAccount("Vik Viewer", "viewer@sathwikflow.dev", passwords.encode(password)));
            var project = projects.create(new ProjectDtos.CreateProjectRequest("SFW", "SathwikFlow Demo", "A seeded workspace showing every project role."), admin.getId());
            projects.addMember("SFW", new ProjectDtos.AddMemberRequest(member.getId(), ProjectRole.MEMBER), admin.getId(), false);
            projects.addMember("SFW", new ProjectDtos.AddMemberRequest(viewer.getId(), ProjectRole.VIEWER), admin.getId(), false);
            issues.create("SFW", new IssueDtos.CreateIssueRequest("Welcome to SathwikFlow", "Drag this issue through the board. Viewers can read but cannot move it.", IssueType.STORY, IssuePriority.HIGH, null, member.getId(), null, null, null, List.of()), admin.getId(), false);
            issues.create("SFW", new IssueDtos.CreateIssueRequest("Review role-based access", "Try the demo accounts to see backend-enforced roles.", IssueType.TASK, IssuePriority.MEDIUM, null, null, null, null, null, List.of()), admin.getId(), false);
        };
    }
}

