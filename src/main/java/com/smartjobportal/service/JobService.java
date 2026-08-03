package com.smartjobportal.service;

import com.smartjobportal.model.Job;
import com.smartjobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public List<Job> searchJobs(String title, String company, String location) {
        // Treat literal "null" or "undefined" as absent parameters (some frontends send these)
        boolean hasTitle = title != null && !title.isBlank() && !title.equalsIgnoreCase("null") && !title.equalsIgnoreCase("undefined");
        boolean hasCompany = company != null && !company.isBlank() && !company.equalsIgnoreCase("null") && !company.equalsIgnoreCase("undefined");
        boolean hasLocation = location != null && !location.isBlank() && !location.equalsIgnoreCase("null") && !location.equalsIgnoreCase("undefined");

        if (!hasTitle && !hasCompany && !hasLocation) {
            return jobRepository.findAll();
        }

        // Load all jobs and apply filters in-memory to support combinations and resilient matching
        return jobRepository.findAll().stream()
                .filter(job -> {
                    if (hasTitle) {
                        String t = job.getTitle() == null ? "" : job.getTitle().toLowerCase();
                        if (!t.contains(title.toLowerCase())) return false;
                    }
                    if (hasCompany) {
                        String c = job.getCompany() == null ? "" : job.getCompany().toLowerCase();
                        if (!c.contains(company.toLowerCase())) return false;
                    }
                    if (hasLocation) {
                        String l = job.getLocation() == null ? "" : job.getLocation().toLowerCase();
                        if (!l.contains(location.toLowerCase())) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public Job updateJob(Long id, Job update) {
        return jobRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(update.getTitle());
                    existing.setCompany(update.getCompany());
                    existing.setLocation(update.getLocation());
                    existing.setDescription(update.getDescription());
                    existing.setRemote(update.isRemote());
                    existing.setRole(update.getRole());
                    existing.setRequiredExperienceYears(update.getRequiredExperienceYears());
                    return jobRepository.save(existing);
                })
                .orElse(null);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}
