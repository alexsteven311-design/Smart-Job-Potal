package com.smartjobportal.controller;

import com.smartjobportal.dto.MockInterviewAnswerRequest;
import com.smartjobportal.dto.MockInterviewStartRequest;
import com.smartjobportal.model.User;
import com.smartjobportal.service.MockInterviewService;
import com.smartjobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates/mock-interviews")
public class MockInterviewController {
    private final MockInterviewService mockInterviewService;
    private final UserService userService;
    public MockInterviewController(MockInterviewService mockInterviewService, UserService userService) { this.mockInterviewService = mockInterviewService; this.userService = userService; }

    @PostMapping
    public ResponseEntity<Map<String, Object>> start(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody MockInterviewStartRequest request) {
        return ResponseEntity.ok(mockInterviewService.start(candidate(principal), request));
    }
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<Map<String, Object>> answer(@AuthenticationPrincipal UserDetails principal, @PathVariable Long questionId, @Valid @RequestBody MockInterviewAnswerRequest request) {
        return ResponseEntity.ok(mockInterviewService.submitAnswer(candidate(principal), questionId, request.getAnswer()));
    }
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<Map<String, Object>> complete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long sessionId) {
        return ResponseEntity.ok(mockInterviewService.complete(candidate(principal), sessionId));
    }
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> history(@AuthenticationPrincipal UserDetails principal) { return ResponseEntity.ok(mockInterviewService.history(candidate(principal))); }
    @GetMapping("/{sessionId}")
    public ResponseEntity<Map<String, Object>> get(@AuthenticationPrincipal UserDetails principal, @PathVariable Long sessionId) { return ResponseEntity.ok(mockInterviewService.get(candidate(principal), sessionId)); }
    private User candidate(UserDetails principal) { return userService.findByEmail(principal.getUsername()).orElseThrow(() -> new IllegalArgumentException("Candidate not found")); }
}
