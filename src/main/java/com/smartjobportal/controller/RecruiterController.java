package com.smartjobportal.controller;

import com.smartjobportal.dto.EmailRequest;
import com.smartjobportal.dto.ScheduleInterviewRequest;
import com.smartjobportal.dto.StatusUpdateRequest;
import com.smartjobportal.model.ApplicationStatus;
import com.smartjobportal.model.Interview;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.User;
import com.smartjobportal.service.RecruiterService;
import com.smartjobportal.service.ResumeService;
import com.smartjobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final UserService userService;
    private final ResumeService resumeService;

    public RecruiterController(RecruiterService recruiterService, UserService userService, ResumeService resumeService) {
        this.recruiterService = recruiterService;
        this.userService = userService;
        this.resumeService = resumeService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> postJob(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody Job job) {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(recruiterService.postJob(job));
    }

    @GetMapping("/jobs/{jobId}/applicants")
    public ResponseEntity<List<JobApplication>> getApplicants(@AuthenticationPrincipal UserDetails principal, @PathVariable Long jobId) {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(recruiterService.getApplicantsForJob(jobId));
    }

    @GetMapping("/applications/{id}/resume")
    public ResponseEntity<Resource> downloadResume(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) throws MalformedURLException {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        Optional<JobApplication> optional = recruiterService.getApplication(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String filename = optional.get().getCandidate().getResumeFilename();
        if (filename == null || filename.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Path file = Paths.get(resumeService.getUploadDir()).resolve(filename).normalize();
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName().toString() + "\"")
                .body(resource);
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<JobApplication> updateStatus(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        Optional<JobApplication> updated = recruiterService.updateApplicationStatus(id, status);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/applications/{id}/schedule")
    public ResponseEntity<Interview> scheduleInterview(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @Valid @RequestBody ScheduleInterviewRequest request) {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        Optional<Interview> interview = recruiterService.scheduleInterview(id, request);
        return interview.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/applications/{id}/email")
    public ResponseEntity<String> sendEmail(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @Valid @RequestBody EmailRequest request) {
        User user = userService.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !("recruiter".equalsIgnoreCase(user.getRole()) || "employer".equalsIgnoreCase(user.getRole()))) {
            return ResponseEntity.status(403).build();
        }
        Optional<JobApplication> application = recruiterService.getApplication(id);
        if (application.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        boolean sent = recruiterService.sendApplicationEmail(application.get(), request);
        return ResponseEntity.ok(sent ? "Email sent" : "Email failed");
    }
}
