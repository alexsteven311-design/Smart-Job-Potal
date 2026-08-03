package com.smartjobportal.service;

import com.smartjobportal.model.Job;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyJobRecommendationScheduler {

    private final UserRepository userRepository;
    private final JobApplicationService jobApplicationService;
    private final NotificationService notificationService;

    public DailyJobRecommendationScheduler(UserRepository userRepository,
                                            JobApplicationService jobApplicationService,
                                            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.jobApplicationService = jobApplicationService;
        this.notificationService = notificationService;
    }

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyJobRecommendations() {
        List<User> candidates = userRepository.findByRoleIgnoreCase("candidate");
        for (User candidate : candidates) {
            if (notificationService.alreadySentToday(candidate, "JOB_RECOMMENDATION")) continue;

            List<Job> recommendations = jobApplicationService.recommendJobs(candidate, false);
            if (recommendations.isEmpty()) continue;

            String jobTitles = recommendations.stream()
                    .limit(3)
                    .map(Job::getTitle)
                    .collect(Collectors.joining(", "));

            String message = "\ud83d\udcbc " + recommendations.size() + " new job" + (recommendations.size() > 1 ? "s" : "")
                    + " recommended for you today: " + jobTitles
                    + (recommendations.size() > 3 ? " and more." : ".");

            notificationService.create(candidate, message, "JOB_RECOMMENDATION");
        }
    }
}
