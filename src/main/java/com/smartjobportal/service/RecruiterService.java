package com.smartjobportal.service;

import com.smartjobportal.dto.EmailRequest;
import com.smartjobportal.dto.ScheduleInterviewRequest;
import com.smartjobportal.model.ApplicationStatus;
import com.smartjobportal.model.Interview;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.Notification;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.InterviewRepository;
import com.smartjobportal.repository.JobApplicationRepository;
import com.smartjobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public RecruiterService(JobRepository jobRepository, JobApplicationRepository applicationRepository,
                            InterviewRepository interviewRepository, EmailService emailService,
                            NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public Job postJob(Job job) {
        return jobRepository.save(job);
    }

    public List<JobApplication> getApplicantsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Optional<JobApplication> getApplication(Long applicationId) {
        return applicationRepository.findById(applicationId);
    }

    public Optional<JobApplication> updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        return applicationRepository.findById(applicationId)
                .map(application -> {
                    ApplicationStatus previousStatus = application.getStatus();
                    application.setStatus(status);
                    JobApplication saved = applicationRepository.save(application);
                    if (status != previousStatus) {
                        emailService.sendApplicationStatusNotification(saved, status);
                        notificationService.create(saved.getCandidate(),
                                "Your application for '" + saved.getJob().getTitle() + "' at " + saved.getJob().getCompany()
                                        + " is now " + status.name().toLowerCase().replace('_', ' ') + ".",
                                status == ApplicationStatus.REVIEWED ? "APPLICATION_SHORTLISTED" : "STATUS_CHANGED");
                    }
                    return saved;
                });
    }
    public Optional<Interview> scheduleInterview(Long applicationId, ScheduleInterviewRequest request) {
        return applicationRepository.findById(applicationId)
                .map(application -> {
                    Interview interview = new Interview();
                    interview.setApplication(application);
                    interview.setScheduledAt(request.getScheduledAt());
                    interview.setNotes(request.getNotes());

                    String meetingPlatform = request.getMeetingPlatform();
                    String location = request.getLocation();
                    String meetingLink = null;
                    if (meetingPlatform != null && !meetingPlatform.isBlank()) {
                        meetingLink = buildMeetingLink(meetingPlatform);
                    } else if (location == null || location.isBlank() || location.toLowerCase().contains("virtual")) {
                        meetingLink = buildMeetingLink("google");
                    } else if (location.toLowerCase().contains("zoom")) {
                        meetingLink = buildMeetingLink("zoom");
                    } else if (location.toLowerCase().contains("meet")) {
                        meetingLink = buildMeetingLink("google");
                    }
                    if (meetingLink != null) {
                        interview.setMeetingLink(meetingLink);
                        if (location == null || location.isBlank()) {
                            interview.setLocation("Virtual - " + (meetingPlatform == null || meetingPlatform.isBlank() ? "Google Meet" : meetingPlatform));
                        } else {
                            interview.setLocation(location);
                        }
                    } else {
                        interview.setLocation(location);
                    }

                    application.setStatus(ApplicationStatus.INTERVIEW);
                    applicationRepository.save(application);
                    Interview savedInterview = interviewRepository.save(interview);
                    emailService.sendInterviewInvite(application, savedInterview);
                    notificationService.create(application.getCandidate(),
                            "📅 Interview scheduled for '" + application.getJob().getTitle() + "' at " + application.getJob().getCompany() + " on " + request.getScheduledAt() + ".",
                            "INTERVIEW_SCHEDULED");
                    return savedInterview;
                });
    }

    private String buildMeetingLink(String meetingPlatform) {
        String normalized = meetingPlatform == null ? "" : meetingPlatform.trim().toLowerCase();
        if (normalized.contains("zoom")) {
            return "https://zoom.us/j/" + generateDigits(10);
        }
        return "https://meet.google.com/" + generateGoogleMeetCode();
    }

    private String generateGoogleMeetCode() {
        return randomAlphaNumeric(3) + "-" + randomAlphaNumeric(4) + "-" + randomAlphaNumeric(3);
    }

    private String generateDigits(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((int) (Math.random() * 10));
        }
        return builder.toString();
    }

    private String randomAlphaNumeric(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            builder.append(chars.charAt(index));
        }
        return builder.toString();
    }

    public boolean sendApplicationEmail(JobApplication application, EmailRequest request) {
        User candidate = application.getCandidate();
        if (candidate == null || candidate.getEmail() == null) {
            return false;
        }
        return emailService.sendEmail(candidate.getEmail(), request.getSubject(), request.getBody());
    }
}
