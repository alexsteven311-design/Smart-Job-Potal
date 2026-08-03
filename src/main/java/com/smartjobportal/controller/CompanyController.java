package com.smartjobportal.controller;

import com.smartjobportal.model.Job;
import com.smartjobportal.repository.JobRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final JobRepository jobRepository;

    public CompanyController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return jobRepository.findAll().stream()
                .filter(job -> job.getCompany() != null && !job.getCompany().isBlank())
                .collect(Collectors.groupingBy(Job::getCompany))
                .entrySet().stream()
                .map(entry -> companySummary(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(company -> (String) company.get("name")))
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Map<String, Object>> profile(@PathVariable String name) {
        List<Job> jobs = jobRepository.findByCompanyContainingIgnoreCase(name).stream()
                .filter(job -> job.getCompany().equalsIgnoreCase(name))
                .toList();
        if (jobs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> profile = companySummary(jobs.get(0).getCompany(), jobs);
        profile.put("openPositions", jobs.stream().sorted(Comparator.comparing(Job::getPostedAt).reversed()).toList());
        profile.put("locations", jobs.stream().map(Job::getLocation).filter(location -> location != null && !location.isBlank()).distinct().toList());
        return ResponseEntity.ok(profile);
    }

    private Map<String, Object> companySummary(String name, List<Job> jobs) {
        int seed = Math.floorMod(name.toLowerCase(Locale.ROOT).hashCode(), 11);
        Map<String, Object> company = new LinkedHashMap<>();
        company.put("name", name);
        company.put("rating", 3.9 + (seed / 10.0));
        company.put("reviewCount", 100 + Math.floorMod(name.hashCode(), 900));
        company.put("openPositionCount", jobs.size());
        company.put("logoUrl", jobs.stream().map(Job::getLogoUrl).filter(url -> url != null && !url.isBlank()).findFirst().orElse(null));
        return company;
    }
}
