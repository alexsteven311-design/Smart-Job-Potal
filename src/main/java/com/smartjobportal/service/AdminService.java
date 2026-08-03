package com.smartjobportal.service;

import com.smartjobportal.model.Job;
import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.dto.ResumeScoreResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserService userService;
    private final JobService jobService;
    private final JobApplicationRepository applicationRepository;
    private final ResumeService resumeService;

    public AdminService(UserService userService, JobService jobService, JobApplicationRepository applicationRepository, ResumeService resumeService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationRepository = applicationRepository;
        this.resumeService = resumeService;
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public List<User> getRecruiters() {
        return userService.getUsersByRole("recruiter");
    }

    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    public void deleteRecruiter(Long id) {
        userService.deleteUser(id);
    }

    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    public Job getJobById(Long id) {
        return jobService.getJobById(id);
    }

    public Job createJob(Job job) {
        return jobService.createJob(job);
    }

    public Job updateJob(Long id, Job update) {
        return jobService.updateJob(id, update);
    }

    public User updateUser(Long id, User update) {
        return userService.updateUser(id, update);
    }

    public User updateRecruiter(Long id, User update) {
        return userService.updateUser(id, update);
    }

    public void removeJob(Long id) {
        jobService.deleteJob(id);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Job> jobs = jobService.getAllJobs();
        List<JobApplication> applications = applicationRepository.findAll();
        List<User> users = userService.getAllUsers();
        stats.put("totalUsers", users.size());
        stats.put("totalRecruiters", userService.getUsersByRole("recruiter").size());
        stats.put("totalCandidates", userService.getUsersByRole("candidate").size());
        stats.put("totalJobs", jobs.size());
        stats.put("totalApplications", applications.size());
        stats.put("applicationsByStatus", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getStatus().name(), Collectors.counting())));
        stats.put("topJobsByApplications", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getJob().getTitle(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList()));
        return stats;
    }

    public Map<String, Object> getApplicationStatistics() {
        List<JobApplication> applications = applicationRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        stats.put("statusCounts", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getStatus().name(), Collectors.counting())));
        stats.put("applicationsPerJob", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getJob().getTitle(), Collectors.counting())));
        stats.put("applicationsPerCandidate", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getCandidate().getEmail(), Collectors.counting())));
        return stats;
    }

    public Map<String, Object> generateReports() {
        List<JobApplication> applications = applicationRepository.findAll();
        Map<String, Object> report = new HashMap<>();
        report.put("summary", getDashboardStats());
        report.put("topJobsByApplications", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getJob().getTitle(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList()));
        report.put("topCandidatesByApplications", applications.stream()
                .collect(Collectors.groupingBy(app -> app.getCandidate().getEmail(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList()));
        report.put("recentApplications", applications.stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .limit(10)
                .map(app -> Map.of(
                        "applicationId", app.getId(),
                        "jobTitle", app.getJob().getTitle(),
                        "candidate", app.getCandidate().getEmail(),
                        "status", app.getStatus().name(),
                        "appliedAt", app.getAppliedAt()))
                .collect(Collectors.toList()));
        return report;
    }

    public Map<String, Object> getSystemMonitoring() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> monitoring = new HashMap<>();
        monitoring.put("javaVersion", System.getProperty("java.version"));
        monitoring.put("osName", System.getProperty("os.name"));
        monitoring.put("availableProcessors", runtime.availableProcessors());
        monitoring.put("uptimeMillis", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime());
        monitoring.put("memoryFree", runtime.freeMemory());
        monitoring.put("memoryTotal", runtime.totalMemory());
        monitoring.put("memoryMax", runtime.maxMemory());
        monitoring.put("memoryUsed", runtime.totalMemory() - runtime.freeMemory());
        monitoring.put("threadCount", java.lang.management.ManagementFactory.getThreadMXBean().getThreadCount());
        monitoring.put("loadedJobCount", jobService.getAllJobs().size());
        monitoring.put("openApplicationCount", applicationRepository.findAll().size());
        return monitoring;
    }

    public ResumeScoreResponse scoreResumeForJob(Long jobId, String skills) {
        ResumeScoreResponse resp = new ResumeScoreResponse();
        if (jobId == null) {
            resp.setJobId(null);
            resp.setMatchPercentage(0);
            resp.setRequiredSkillCount(0);
            resp.setMatchedCount(0);
            resp.setMatchedSkills(List.of());
            resp.setDetails(Map.of("error", "job id is required"));
            return resp;
        }
        
        resp.setJobId(jobId);
        try {
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                resp.setMatchPercentage(0);
                resp.setRequiredSkillCount(0);
                resp.setMatchedCount(0);
                resp.setMatchedSkills(List.of());
                resp.setDetails(Map.of("error", "job not found"));
                return resp;
            }

            String title = job.getTitle() != null ? job.getTitle() : "";
            String description = job.getDescription() != null ? job.getDescription() : "";
            String text = (title + " " + description).toLowerCase().trim();
            if (text.isBlank()) {
                resp.setMatchPercentage(0);
                resp.setRequiredSkillCount(0);
                resp.setMatchedCount(0);
                resp.setMatchedSkills(List.of());
                resp.setDetails(Map.of("error", "job has no title or description"));
                return resp;
            }

            String[] tokens = text.split("[^a-z0-9+#.+-]");
            List<String> required = Arrays.stream(tokens)
                    .map(String::trim)
                    .filter(s -> !s.isBlank() && s.length() > 1)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> resumeSkills = skills == null || skills.isBlank() ? List.of() : Arrays.stream(skills.split(","))
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            List<String> matched = required.stream()
                    .filter(req -> resumeSkills.stream().anyMatch(rs -> rs.contains(req) || req.contains(rs) || rs.equals(req)))
                    .collect(Collectors.toList());

            int requiredCount = required.size();
            int matchedCount = matched.size();
            double matchPct = requiredCount == 0 ? (matchedCount > 0 ? 100.0 : 0.0) : (matchedCount * 100.0 / requiredCount);

            resp.setRequiredSkillCount(requiredCount);
            resp.setMatchedCount(matchedCount);
            resp.setMatchedSkills(matched);
            resp.setMatchPercentage(Math.round(matchPct * 100.0) / 100.0);

            double resumeScore = resumeService.computeResumeScore(String.join(",", resumeSkills), 0);
            resp.setDetails(Map.of(
                    "resumeScoreHeuristic", resumeScore,
                    "requiredSample", required.stream().limit(20).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            System.err.println("Error scoring resume for job: " + e.getMessage());
            resp.setMatchPercentage(0);
            resp.setRequiredSkillCount(0);
            resp.setMatchedCount(0);
            resp.setMatchedSkills(List.of());
            resp.setDetails(Map.of("error", "Error scoring resume: " + e.getMessage()));
        }

        return resp;
    }
}
