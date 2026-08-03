package com.smartjobportal.controller;

import com.smartjobportal.dto.ResumeParseResponse;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.User;
import com.smartjobportal.service.JobService;
import com.smartjobportal.service.ResumeService;
import com.smartjobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/candidates")
public class CoverLetterController {

    private final UserService userService;
    private final ResumeService resumeService;
    private final JobService jobService;

    public CoverLetterController(UserService userService, ResumeService resumeService, JobService jobService) {
        this.userService = userService;
        this.resumeService = resumeService;
        this.jobService = jobService;
    }

    @PostMapping("/cover-letter/generate")
    public ResponseEntity<Map<String, Object>> generateCoverLetter(@AuthenticationPrincipal UserDetails principal,
                                                                   @RequestParam("file") MultipartFile file,
                                                                   @RequestParam("jobId") Long jobId) throws IOException {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Candidate not found"));
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Job not found"));
        }

        ResumeParseResponse parsed = resumeService.parseResumeFile(file);
        String coverLetter = buildCoverLetter(parsed, candidate, job);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("coverLetter", coverLetter);
        response.put("jobTitle", job.getTitle());
        response.put("company", job.getCompany());
        return ResponseEntity.ok(response);
    }

    private String buildCoverLetter(ResumeParseResponse parsed, User candidate, Job job) {
        String candidateName = parsed.getName() != null && !parsed.getName().isBlank() ? parsed.getName() : (candidate.getName() != null ? candidate.getName() : "Candidate");
        String email = parsed.getEmail() != null && !parsed.getEmail().isBlank() ? parsed.getEmail() : (candidate.getEmail() != null ? candidate.getEmail() : "your.email@example.com");
        List<String> skills = parsed.getSkills() != null ? parsed.getSkills() : List.of();
        List<String> experience = parsed.getExperience() != null ? parsed.getExperience() : List.of();

        String skillsText = skills.stream().limit(6).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.joining(", "));
        String experienceText = experience.stream().limit(3).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.joining("; "));
        if (experienceText.isBlank()) {
            experienceText = "relevant professional experience and a strong commitment to continuous learning";
        }
        if (skillsText.isBlank()) {
            skillsText = "problem-solving, collaboration, and adaptability";
        }

        return "Dear Hiring Manager,\n\n"
                + "I am excited to apply for the " + job.getTitle() + " position at " + job.getCompany() + ". As a motivated professional, I believe my background and experience make me a strong fit for this opportunity.\n\n"
                + "Through my experience, I have developed strengths in " + skillsText + ". I have also built a track record of delivering results through " + experienceText + ".\n\n"
                + "I am particularly drawn to this role because of the opportunity to contribute to " + job.getCompany() + " and grow within a team that values innovation and impact. I would welcome the opportunity to discuss how my skills and experience align with your needs.\n\n"
                + "Thank you for your time and consideration. I look forward to the opportunity to speak with you further.\n\n"
                + "Sincerely,\n"
                + candidateName + "\n"
                + email;
    }
}
