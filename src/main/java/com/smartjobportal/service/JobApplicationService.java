package com.smartjobportal.service;

import com.smartjobportal.model.ApplicationStatus;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {
    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public JobApplicationService(JobApplicationRepository applicationRepository, JobRepository jobRepository,
                                  EmailService emailService, @Lazy NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public JobApplication applyForJob(User candidate, Job job, String coverLetter) {
        if (applicationRepository.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) {
            return null;
        }
        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setCoverLetter(coverLetter);
        application.setStatus(ApplicationStatus.APPLIED);
        JobApplication saved = applicationRepository.save(application);
        // Fire email + notification asynchronously — never block the apply response
        new Thread(() -> {
            try { emailService.sendApplicationSubmittedNotification(saved); } catch (Exception ignored) {}
            try {
                notificationService.create(candidate,
                    "Your application for '" + job.getTitle() + "' at " + job.getCompany() + " was submitted successfully.",
                    "APPLICATION_SUBMITTED");
            } catch (Exception ignored) {}
        }).start();
        return saved;
    }

    public List<JobApplication> getApplicationsForCandidate(User candidate) {
        return applicationRepository.findByCandidateOrderByIdDesc(candidate);
    }

    public JobApplication getApplication(Long id, User candidate) {
        return applicationRepository.findById(id)
                .filter(app -> app.getCandidate().getId().equals(candidate.getId()))
                .orElse(null);
    }

    public List<Job> recommendJobs(User candidate, boolean includePreviousApplications) {
        List<String> skills = candidate.getSkills() == null ? List.of() : List.of(candidate.getSkills().split(","));
        List<String> normalizedSkills = skills.stream()
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        String preferredRole = candidate.getPreferredRole() == null ? "" : candidate.getPreferredRole().trim().toLowerCase();
        Integer experienceYears = candidate.getExperienceYears();

        Set<Long> appliedJobIds = new HashSet<>();
        Set<String> appliedJobTitles = new HashSet<>();
        Set<String> appliedJobRoles = new HashSet<>();

        getApplicationsForCandidate(candidate).forEach(application -> {
            if (application.getJob() != null) {
                if (application.getJob().getId() != null) {
                    appliedJobIds.add(application.getJob().getId());
                }
                if (includePreviousApplications) {
                    if (application.getJob().getTitle() != null) {
                        appliedJobTitles.add(application.getJob().getTitle().toLowerCase());
                    }
                    if (application.getJob().getRole() != null) {
                        appliedJobRoles.add(application.getJob().getRole().toLowerCase());
                    }
                }
            }
        });

        return jobRepository.findAll().stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .map(job -> new JobRecommendation(job, scoreJob(job, normalizedSkills, preferredRole, experienceYears, appliedJobTitles, appliedJobRoles, includePreviousApplications)))
                .filter(rec -> rec.score > 0)
                .sorted(Comparator.comparingDouble((JobRecommendation rec) -> rec.score).reversed())
                .limit(10)
                .map(rec -> rec.job)
                .collect(Collectors.toList());
    }

    private double scoreJob(Job job,
                            List<String> skills,
                            String preferredRole,
                            Integer experienceYears,
                            Set<String> appliedJobTitles,
                            Set<String> appliedJobRoles,
                            boolean includePreviousApplications) {
        double score = 0;
        String title = job.getTitle() == null ? "" : job.getTitle().toLowerCase();
        String description = job.getDescription() == null ? "" : job.getDescription().toLowerCase();
        String company = job.getCompany() == null ? "" : job.getCompany().toLowerCase();
        String location = job.getLocation() == null ? "" : job.getLocation().toLowerCase();
        String jobRole = job.getRole() == null ? "" : job.getRole().toLowerCase();
        String content = String.join(" ", title, description, company, location, jobRole);

        for (String skill : skills) {
            if (content.contains(skill)) {
                score += 10;
            }
        }

        if (!preferredRole.isBlank()) {
            if (jobRole.contains(preferredRole) || title.contains(preferredRole) || description.contains(preferredRole)) {
                score += 30;
            }
        }

        if (experienceYears != null) {
            Integer required = job.getRequiredExperienceYears();
            if (required != null) {
                int diff = Math.abs(required - experienceYears);
                if (diff <= 1) {
                    score += 20;
                } else if (diff <= 3) {
                    score += 12;
                } else if (diff <= 5) {
                    score += 6;
                }
            } else {
                score += 5;
            }
        }

        if (includePreviousApplications) {
            boolean similarToPrevious = appliedJobTitles.stream().anyMatch(title::contains)
                    || appliedJobRoles.stream().anyMatch(jobRole::contains);
            if (similarToPrevious) {
                score += 18;
            }
        }

        return score;
    }

    private static class JobRecommendation {
        private final Job job;
        private final double score;

        public JobRecommendation(Job job, double score) {
            this.job = job;
            this.score = score;
        }
    }
}
