package com.smartjobportal.controller;

import com.smartjobportal.model.Job;
import com.smartjobportal.model.SavedJob;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.SavedJobRepository;
import com.smartjobportal.service.JobService;
import com.smartjobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SavedJobController {

    private final UserService userService;
    private final JobService jobService;
    private final SavedJobRepository savedJobRepository;

    public SavedJobController(UserService userService, JobService jobService, SavedJobRepository savedJobRepository) {
        this.userService = userService;
        this.jobService = jobService;
        this.savedJobRepository = savedJobRepository;
    }

    @GetMapping("/api/candidates/saved-jobs")
    public ResponseEntity<List<Map<String, Object>>> getSavedJobs(@AuthenticationPrincipal UserDetails principal) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }

        List<SavedJob> savedJobs = savedJobRepository.findByUserIdOrderBySavedAtDesc(candidate.getId());
        List<Map<String, Object>> response = new ArrayList<>();
        for (SavedJob savedJob : savedJobs) {
            Job job = savedJob.getJob();
            if (job != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", savedJob.getId());
                item.put("job", job);
                response.add(item);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/candidates/saved-jobs/{jobId}")
    public ResponseEntity<Map<String, Object>> saveJob(@AuthenticationPrincipal UserDetails principal,
                                                       @PathVariable Long jobId) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Candidate not found"));
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Job not found"));
        }

        if (savedJobRepository.existsByUserIdAndJobId(candidate.getId(), jobId)) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Job already saved"));
        }

        SavedJob savedJob = savedJobRepository.save(new SavedJob(candidate, job));
        return ResponseEntity.ok(Map.of("success", true, "message", "Job saved successfully", "savedJob", savedJob));
    }

    @DeleteMapping("/api/candidates/saved-jobs/{jobId}")
    public ResponseEntity<Map<String, Object>> removeSavedJob(@AuthenticationPrincipal UserDetails principal,
                                                              @PathVariable Long jobId) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Candidate not found"));
        }

        savedJobRepository.deleteByUserIdAndJobId(candidate.getId(), jobId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Job removed from saved list"));
    }
}
