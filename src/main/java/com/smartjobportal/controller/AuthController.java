package com.smartjobportal.controller;

import com.smartjobportal.dto.AuthResponse;
import com.smartjobportal.dto.LoginRequest;
import com.smartjobportal.dto.RegisterRequest;
import com.smartjobportal.model.User;
import com.smartjobportal.service.UserService;
import com.smartjobportal.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        String role = normalizeSelfRegistrationRole(request.getRole());
        user.setRole(role);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLocation(request.getLocation());
        user.setLinkedInProfileUrl(request.getLinkedInProfileUrl());
        user.setGithubPortfolioUrl(request.getGithubPortfolioUrl());
        user.setPortfolioWebsite(request.getPortfolioWebsite());
        user.setSkills(request.getSkills());
        user.setExperienceYears(request.getExperienceYears());
        user.setPreferredRole(request.getPreferredRole());
        // Store company name in preferredRole field for recruiters
        if (StringUtils.hasText(request.getCompany()) && !StringUtils.hasText(request.getPreferredRole())) {
            user.setPreferredRole(request.getCompany());
        }
        User saved = userService.createUser(user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token, saved.getEmail(), saved.getName(), saved.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);
        User user = userService.findByEmail(request.getEmail()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getName(), user.getRole()));
    }

    private String normalizeSelfRegistrationRole(String requestedRole) {
        if (!StringUtils.hasText(requestedRole)) {
            return "candidate";
        }
        String role = requestedRole.trim().toLowerCase();
        if (role.equals("candidate") || role.equals("recruiter") || role.equals("employer")) {
            return role;
        }
        throw new IllegalArgumentException("Role must be candidate, recruiter, or employer");
    }
}
