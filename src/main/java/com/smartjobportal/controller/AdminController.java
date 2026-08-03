package com.smartjobportal.controller;

import com.smartjobportal.model.Job;
import com.smartjobportal.model.User;
import com.smartjobportal.dto.ResumeScoreRequest;
import com.smartjobportal.dto.ResumeScoreResponse;
import com.smartjobportal.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updated = adminService.updateUser(id, user);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recruiters")
    public ResponseEntity<List<User>> getRecruiters() {
        return ResponseEntity.ok(adminService.getRecruiters());
    }

    @PutMapping("/recruiters/{id}")
    public ResponseEntity<User> updateRecruiter(@PathVariable Long id, @Valid @RequestBody User recruiter) {
        User updated = adminService.updateRecruiter(id, recruiter);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/recruiters/{id}")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable Long id) {
        adminService.deleteRecruiter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        Job job = adminService.getJobById(id);
        return job != null ? ResponseEntity.ok(job) : ResponseEntity.notFound().build();
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.ok(adminService.createJob(job));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @Valid @RequestBody Job job) {
        Job updated = adminService.updateJob(id, job);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> removeJob(@PathVariable Long id) {
        adminService.removeJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/applications/statistics")
    public ResponseEntity<Map<String, Object>> getApplicationStatistics() {
        return ResponseEntity.ok(adminService.getApplicationStatistics());
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        return ResponseEntity.ok(adminService.generateReports());
    }

    @GetMapping("/monitoring")
    public ResponseEntity<Map<String, Object>> getMonitoring() {
        return ResponseEntity.ok(adminService.getSystemMonitoring());
    }

    @PostMapping("/score-resume")
    public ResponseEntity<ResumeScoreResponse> scoreResume(@Valid @RequestBody ResumeScoreRequest req) {
        ResumeScoreResponse resp = adminService.scoreResumeForJob(req.getJobId(), req.getSkills());
        return ResponseEntity.ok(resp);
    }
}
