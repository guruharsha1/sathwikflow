package com.example.jiralite.auth.application;

import com.example.jiralite.auth.api.AuthController.AuthResponse;
import com.example.jiralite.auth.api.AuthController.LoginRequest;
import com.example.jiralite.auth.api.AuthController.ProfileResponse;
import com.example.jiralite.auth.api.AuthController.RegisterRequest;
import com.example.jiralite.user.UserAccount;
import com.example.jiralite.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        users.findByEmail(email).ifPresent(existing -> {
            throw new IllegalArgumentException("Email is already registered");
        });
        UserAccount user = users.save(new UserAccount(request.displayName(), email, passwordEncoder.encode(request.password())));
        return new AuthResponse("development-token", new ProfileResponse(user.getId().toString(), user.getDisplayName()));
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = users.findByEmail(request.email().toLowerCase())
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return new AuthResponse("development-token", new ProfileResponse(user.getId().toString(), user.getDisplayName()));
    }
}
