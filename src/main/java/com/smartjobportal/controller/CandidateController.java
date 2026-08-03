package com.smartjobportal.controller;

import com.smartjobportal.dto.JobApplicationRequest;
import com.smartjobportal.dto.ResumeParseRequest;
import com.smartjobportal.dto.ResumeParseResponse;
import com.smartjobportal.model.Interview;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.InterviewRepository;
import com.smartjobportal.service.JobApplicationService;
import com.smartjobportal.service.JobService;
import com.smartjobportal.service.ResumeService;
import com.smartjobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final UserService userService;
    private final JobService jobService;
    private final JobApplicationService jobApplicationService;
    private final ResumeService resumeService;
    private final InterviewRepository interviewRepository;

    public CandidateController(UserService userService, JobService jobService,
                                JobApplicationService jobApplicationService,
                                ResumeService resumeService,
                                InterviewRepository interviewRepository) {
        this.userService = userService;
        this.jobService = jobService;
        this.jobApplicationService = jobApplicationService;
        this.resumeService = resumeService;
        this.interviewRepository = interviewRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails principal) {
        return userService.findByEmail(principal.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody User update) {
        User current = userService.findByEmail(principal.getUsername()).orElse(null);
        if (current == null) {
            return ResponseEntity.notFound().build();
        }
        update.setId(current.getId());
        update.setEmail(current.getEmail());
        update.setRole(current.getRole());
        User updated = userService.updateUser(current.getId(), update);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/profile/upload-resume")
    public ResponseEntity<Map<String, Object>> uploadResume(@AuthenticationPrincipal UserDetails principal, @RequestParam("file") MultipartFile file) throws IOException {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }
        User saved = resumeService.storeResume(candidate, file);
        ResumeParseResponse parsed = resumeService.parseResumeFile(file);
        Map<String, Object> response = Map.of(
                "user", saved,
                "parsedResume", parsed
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/parse-resume")
    public ResponseEntity<ResumeParseResponse> parseResume(@Valid @RequestBody ResumeParseRequest request) {
        return ResponseEntity.ok(resumeService.parseResumeText(request.getResumeText()));
    }

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<Map<String, Object>> applyForJob(@AuthenticationPrincipal UserDetails principal,
                                                            @PathVariable Long jobId,
                                                            @Valid @RequestBody JobApplicationRequest request) {
        Map<String, Object> body = new HashMap<>();

        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            body.put("success", false);
            body.put("message", "Candidate not found");
            return ResponseEntity.status(404).body(body);
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            body.put("success", false);
            body.put("message", "Job not found");
            return ResponseEntity.status(404).body(body);
        }

        JobApplication application = jobApplicationService.applyForJob(candidate, job, request.getCoverLetter());
        if (application == null) {
            // Duplicate application: treat as friendly info so the UI can show it
            body.put("success", true);
            body.put("message", "You have already applied for this job");
            return ResponseEntity.status(409).body(body);
        }


        body.put("success", true);
        body.put("message", "Application submitted successfully");
        body.put("application", application);
        return ResponseEntity.ok(body);
    }


    @GetMapping("/applications")
    public ResponseEntity<List<JobApplication>> getApplications(@AuthenticationPrincipal UserDetails principal) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jobApplicationService.getApplicationsForCandidate(candidate));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<JobApplication> getApplication(@AuthenticationPrincipal UserDetails principal,
                                                         @PathVariable Long applicationId) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }
        JobApplication application = jobApplicationService.getApplication(applicationId, candidate);
        return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Job>> getRecommendedJobs(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(name = "includePreviousApplications", defaultValue = "true") boolean includePreviousApplications) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jobApplicationService.recommendJobs(candidate, includePreviousApplications));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal UserDetails principal) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) return ResponseEntity.notFound().build();

        List<JobApplication> applications = jobApplicationService.getApplicationsForCandidate(candidate);
        List<Interview> upcomingInterviews = interviewRepository.findUpcomingByCandidateId(candidate.getId(), LocalDateTime.now());
        List<Job> recommendations = jobApplicationService.recommendJobs(candidate, true);

        // Profile completion score
        int fields = 0;
        if (candidate.getName()               != null && !candidate.getName().isBlank())               fields++;
        if (candidate.getEmail()              != null && !candidate.getEmail().isBlank())              fields++;
        if (candidate.getPhoneNumber()        != null && !candidate.getPhoneNumber().isBlank())        fields++;
        if (candidate.getLocation()           != null && !candidate.getLocation().isBlank())           fields++;
        if (candidate.getSkills()             != null && !candidate.getSkills().isBlank())             fields++;
        if (candidate.getPreferredRole()      != null && !candidate.getPreferredRole().isBlank())      fields++;
        if (candidate.getExperienceYears()    != null)                                                 fields++;
        if (candidate.getLinkedInProfileUrl() != null && !candidate.getLinkedInProfileUrl().isBlank()) fields++;
        if (candidate.getGithubPortfolioUrl() != null && !candidate.getGithubPortfolioUrl().isBlank()) fields++;
        if (candidate.getResumeFilename()     != null && !candidate.getResumeFilename().isBlank())     fields++;
        int profileCompletion = (int) Math.round(fields * 100.0 / 10);

        // Recent activity from last 5 applications
        List<Map<String, String>> recentActivity = applications.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(5)
                .map(app -> {
                    Map<String, String> entry = new HashMap<>();
                    String status = app.getStatus() == null ? "APPLIED" : app.getStatus().name();
                    String icon = switch (status) {
                        case "INTERVIEW"  -> "📅";
                        case "SELECTED"   -> "🎉";
                        case "REJECTED"   -> "❌";
                        case "REVIEWED"   -> "👀";
                        default           -> "📨";
                    };
                    String label = switch (status) {
                        case "INTERVIEW"  -> "Interview scheduled for " + app.getJob().getTitle();
                        case "SELECTED"   -> "Selected for " + app.getJob().getTitle();
                        case "REJECTED"   -> "Not selected for " + app.getJob().getTitle();
                        case "REVIEWED"   -> "Application reviewed: " + app.getJob().getTitle();
                        default           -> "Applied for " + app.getJob().getTitle();
                    };
                    entry.put("icon", icon);
                    entry.put("label", label);
                    entry.put("company", app.getJob().getCompany() != null ? app.getJob().getCompany() : "");
                    return entry;
                })
                .collect(java.util.stream.Collectors.toList());

        // Add resume upload to activity if present
        if (candidate.getResumeFilename() != null && !candidate.getResumeFilename().isBlank()) {
            Map<String, String> resumeEntry = new HashMap<>();
            resumeEntry.put("icon", "📄");
            resumeEntry.put("label", "Resume uploaded");
            resumeEntry.put("company", "");
            recentActivity.add(resumeEntry);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("name",                candidate.getName());
        dashboard.put("profileCompletion",   profileCompletion);
        dashboard.put("resumeScore",         candidate.getResumeScore() != null ? candidate.getResumeScore().intValue() : 0);
        dashboard.put("applicationCount",    applications.size());
        dashboard.put("upcomingInterviews",  upcomingInterviews.size());
        dashboard.put("recommendations",     recommendations.stream().limit(5).map(j -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id",       j.getId());
            m.put("title",    j.getTitle()    != null ? j.getTitle()    : "");
            m.put("company",  j.getCompany()  != null ? j.getCompany()  : "");
            m.put("location", j.getLocation() != null ? j.getLocation() : "");
            return m;
        }).collect(java.util.stream.Collectors.toList()));
        dashboard.put("recentActivity",      recentActivity);
        return ResponseEntity.ok(dashboard);
    }

    // ─── Resume Score ──────────────────────────────────────────────

    @GetMapping("/resume-score")
    public ResponseEntity<Map<String, Object>> getResumeScore(@AuthenticationPrincipal UserDetails principal) {
        User candidate = userService.findByEmail(principal.getUsername()).orElse(null);
        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("resumeFilename", candidate.getResumeFilename());
        response.put("resumeScore", candidate.getResumeScore());
        response.put("skills", candidate.getSkills());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resume-analyze")
    public ResponseEntity<Map<String, Object>> analyzeResume(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "jobTitle", required = false, defaultValue = "") String jobTitle) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "A resume file is required for analysis."));
            }

            // Validate file size (limit to 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "File too large. Maximum 10MB allowed."));
            }

            String text = extractText(file);
            if (text == null || text.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not extract text from file."));
            }

            ResumeParseResponse parsed = resumeService.parseResumeText(text);
            if (parsed == null) {
                parsed = new ResumeParseResponse();
                parsed.setName("");
                parsed.setEmail("");
                parsed.setSkills(List.of());
                parsed.setEducation(List.of());
                parsed.setExperience(List.of());
            }

            String lower = text.toLowerCase();

            // --- Skills: combine parsed skills + keyword scan ---
            List<String> techSkills = List.of(
                "java","python","javascript","typescript","angular","react","spring","spring boot","node","node.js",
                "sql","mysql","postgresql","mongodb","aws","azure","gcp","docker","kubernetes","git","linux",
                "html","css","rest","restful","api","microservices","hibernate","maven","gradle","jenkins","ci/cd",
                "machine learning","deep learning","tensorflow","pytorch","pandas","numpy","spark","kafka",
                "redis","graphql","flutter","kotlin","swift","c++","c#",".net","php","ruby","scala","go",
                "selenium","junit","testng","jira","agile","scrum","devops","terraform","ansible"
            );
            List<String> scannedSkills = techSkills.stream().filter(lower::contains).collect(java.util.stream.Collectors.toList());
            // Merge parsed skills with scanned skills (deduplicated)
            java.util.Set<String> allSkillsSet = new java.util.LinkedHashSet<>();
            List<String> parsedSkills = parsed.getSkills();
            if (parsedSkills != null) parsedSkills.stream().map(String::toLowerCase).forEach(allSkillsSet::add);
            allSkillsSet.addAll(scannedSkills);
            List<String> foundSkills = new java.util.ArrayList<>(allSkillsSet);
            int skillScore = Math.min(100, foundSkills.size() * 6);

            // --- ATS sections: check parsed sections + keyword presence (with null-safety) ---
            List<String> parsedExp = parsed.getExperience();
            List<String> parsedEdu = parsed.getEducation();
            List<String> parsedSkillsNullSafe = parsed.getSkills();
            boolean hasExperience  = (parsedExp != null && !parsedExp.isEmpty()) || lower.contains("experience");
            boolean hasEducation   = (parsedEdu != null && !parsedEdu.isEmpty())  || lower.contains("education");
            boolean hasSkills      = (parsedSkillsNullSafe != null && !parsedSkillsNullSafe.isEmpty())     || lower.contains("skills");
            boolean hasSummary     = lower.contains("summary") || lower.contains("objective") || lower.contains("profile");
            boolean hasCerts       = lower.contains("certification") || lower.contains("certificate") || lower.contains("certified");
            boolean hasProjects    = lower.contains("project");
            String parsedEmail = parsed.getEmail();
            boolean hasContact     = parsedEmail != null && !parsedEmail.isBlank();
            boolean hasPhone       = lower.matches(".*[+]?\\d[\\d\\s\\-]{8,}\\d.*") || lower.contains("phone");
            boolean hasLinkedIn    = lower.contains("linkedin") || lower.contains("linkedin.com");
            boolean hasGitHub      = lower.contains("github") || lower.contains("github.com");
            boolean hasAchievements = lower.contains("achievement") || lower.contains("award") || lower.contains("accomplishment");

            List<String> atsKeywords = List.of("experience","education","skills","summary","certifications","projects","contact","phone","linkedin","achievements");
            // List.of rejects null values, but absent ATS sections are represented as null
            // until they are filtered out below.
            List<String> atsPresence = java.util.Arrays.asList(
                hasExperience?"experience":null, hasEducation?"education":null, hasSkills?"skills":null,
                hasSummary?"summary":null, hasCerts?"certifications":null, hasProjects?"projects":null,
                hasContact?"contact":null, hasPhone?"phone":null, hasLinkedIn?"linkedin":null, hasAchievements?"achievements":null
            );
            List<String> foundAts   = atsPresence.stream().filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toList());
            List<String> missingAts = new java.util.ArrayList<>();
            if (!hasExperience)   missingAts.add("experience");
            if (!hasEducation)    missingAts.add("education");
            if (!hasSkills)       missingAts.add("skills");
            if (!hasSummary)      missingAts.add("summary / objective");
            if (!hasCerts)        missingAts.add("certifications");
            if (!hasProjects)     missingAts.add("projects");
            if (!hasContact)      missingAts.add("email");
            if (!hasPhone)        missingAts.add("phone");
            if (!hasLinkedIn)     missingAts.add("linkedin");
            int atsScore = atsKeywords.isEmpty() ? 0 : (int) Math.round(foundAts.size() * 100.0 / atsKeywords.size());

            // --- Experience level: use parsed experience entries + text signals ---
            int expScore;
            String expLevel;
            List<String> expList = parsed.getExperience();
            int parsedExpCount = expList == null ? 0 : expList.size();
            if (lower.contains("10+ years") || lower.contains("10 years") || (lower.contains("senior") && lower.contains("lead"))) {
                expScore = 100; expLevel = "Senior (10+ years)";
            } else if (lower.contains("senior") || lower.contains("7 year") || lower.contains("8 year") || lower.contains("9 year")) {
                expScore = 85; expLevel = "Senior (7-10 years)";
            } else if (lower.contains("mid") || lower.contains("4 year") || lower.contains("5 year") || lower.contains("6 year")) {
                expScore = 65; expLevel = "Mid Level (4-6 years)";
            } else if (lower.contains("2 year") || lower.contains("3 year") || lower.contains("junior")) {
                expScore = 45; expLevel = "Junior (2-3 years)";
            } else if (lower.contains("fresher") || lower.contains("intern") || lower.contains("trainee") || lower.contains("0-1")) {
                expScore = 25; expLevel = "Fresher / Intern";
            } else if (parsedExpCount >= 3) {
                expScore = 70; expLevel = "Experienced (" + parsedExpCount + " roles)";
            } else if (parsedExpCount > 0) {
                expScore = 50; expLevel = "Experienced";
            } else {
                expScore = 20; expLevel = "Entry Level";
            }

            // --- Keyword match against job title ---
            int keywordScore = 50;
            List<String> matchedKeywords = new java.util.ArrayList<>();
            List<String> missingKeywords = new java.util.ArrayList<>();
            if (!jobTitle.isBlank()) {
                String[] titleTokens = jobTitle.toLowerCase().split("[\\s,]+");
                for (String token : titleTokens) {
                    if (token.length() > 2) {
                        if (lower.contains(token)) matchedKeywords.add(token);
                        else missingKeywords.add(token);
                    }
                }
                int total = matchedKeywords.size() + missingKeywords.size();
                keywordScore = total == 0 ? 50 : (int) Math.round(matchedKeywords.size() * 100.0 / total);
            }

            // --- Overall score ---
            int overall = (skillScore + atsScore + expScore + keywordScore) / 4;

            // --- Resume-specific suggestions based on actual parsed content ---
            List<String> suggestions = new java.util.ArrayList<>();

            if (foundSkills.isEmpty())
                suggestions.add("No technical skills detected. Add a dedicated Skills section with languages, frameworks and tools.");
            else if (skillScore < 40)
                suggestions.add("Only " + foundSkills.size() + " skill(s) detected. Expand your Skills section with more technologies relevant to your role.");

            if (!hasExperience)
                suggestions.add("Add a Work Experience section with your job titles, companies and responsibilities.");
            else if (parsedExpCount > 0 && !lower.contains("%") && !lower.contains("increased") && !lower.contains("reduced") && !lower.contains("improved"))
                suggestions.add("Quantify your " + parsedExpCount + " experience entries with metrics (e.g. 'Improved performance by 30%').");

            if (!hasEducation)
                suggestions.add("Add an Education section with your degree, institution and graduation year.");

            if (!hasSummary)
                suggestions.add("Add a professional Summary or Objective at the top to give recruiters a quick overview.");

            if (!hasCerts && foundSkills.stream().anyMatch(s -> List.of("aws","azure","gcp","java","python","kubernetes").contains(s)))
                suggestions.add("You have cloud/tech skills — add relevant certifications (e.g. AWS Certified, Oracle Java) to strengthen your profile.");

            if (!hasProjects)
                suggestions.add("Add a Projects section to showcase real-world work, especially if you have limited experience.");

            if (!hasLinkedIn)
                suggestions.add("Add your LinkedIn profile URL to improve recruiter reach.");

            if (!hasGitHub && foundSkills.stream().anyMatch(s -> List.of("java","python","javascript","react","angular","node","spring").contains(s)))
                suggestions.add("Add your GitHub profile URL to let recruiters see your code.");

            if (!hasPhone)
                suggestions.add("Add a phone number to your contact details.");

            String parsedName = parsed.getName();
            if (parsedName == null || parsedName.isBlank())
                suggestions.add("Make sure your full name is clearly visible at the top of your resume.");

            if (text.length() < 400)
                suggestions.add("Your resume appears very short (" + text.length() + " characters). Add more detail about your experience, projects and skills.");
            else if (text.length() > 6000)
                suggestions.add("Your resume is quite long. Consider trimming it to 1-2 pages focusing on the most relevant experience.");

            if (!missingKeywords.isEmpty())
                suggestions.add("Add these job-relevant keywords for '" + jobTitle + "': " + missingKeywords.stream().limit(5).collect(java.util.stream.Collectors.joining(", ")));

            Map<String, Object> result = new HashMap<>();
            result.put("overallScore",       overall);
            result.put("skillScore",         skillScore);
            result.put("atsScore",           atsScore);
            result.put("experienceScore",    expScore);
            result.put("keywordScore",       keywordScore);
            result.put("expLevel",           expLevel);
            result.put("foundSkills",        foundSkills);
            result.put("matchedKeywords",    matchedKeywords);
            result.put("missingKeywords",    missingKeywords);
            result.put("missingAtsSections", missingAts);
            result.put("suggestions",        suggestions);
            result.put("parsedName",         parsedName);
            result.put("parsedEmail",        parsedEmail);
            result.put("parsedSkills",       parsedSkillsNullSafe);
            result.put("parsedExperience",   parsedExp);
            result.put("parsedEducation",    parsedEdu);

            // --- Job suggestions from DB matched against found skills + job title ---
            List<Map<String, Object>> jobSuggestions = new java.util.ArrayList<>();
            try {
                List<com.smartjobportal.model.Job> allJobs = jobService.getAllJobs();
                if (allJobs != null && !allJobs.isEmpty()) {
                    jobSuggestions = allJobs.stream()
                            .filter(Objects::nonNull)
                            .map(job -> {
                                String title = job.getTitle() == null ? "" : job.getTitle().toLowerCase();
                                String desc = job.getDescription() == null ? "" : job.getDescription().toLowerCase();
                                String role = job.getRole() == null ? "" : job.getRole().toLowerCase();
                                String content = String.join(" ", title, desc, role);
                                long matchCount = foundSkills.stream().filter(Objects::nonNull).filter(content::contains).count();
                                boolean titleMatch = !jobTitle.isBlank() && content.contains(jobTitle.toLowerCase());
                                return new Object[]{ job, matchCount + (titleMatch ? 10 : 0) };
                            })
                            .filter(pair -> pair != null && pair.length >= 2 && (long) pair[1] > 0)
                            .sorted((a, b) -> Long.compare((long) b[1], (long) a[1]))
                            .limit(6)
                            .map(pair -> {
                                com.smartjobportal.model.Job j = (com.smartjobportal.model.Job) pair[0];
                                Map<String, Object> m = new HashMap<>();
                                m.put("id",       j.getId());
                                m.put("title",    j.getTitle() != null ? j.getTitle() : "");
                                m.put("company",  j.getCompany() != null ? j.getCompany() : "");
                                m.put("location", j.getLocation() != null ? j.getLocation() : "");
                                m.put("remote",   j.isRemote());
                                m.put("role",     j.getRole() != null ? j.getRole() : "");
                                m.put("requiredExperienceYears", j.getRequiredExperienceYears());
                                return m;
                            })
                            .collect(java.util.stream.Collectors.toList());
                }
            } catch (Exception e) {
                // Log error but don't fail the entire response
                System.err.println("Error fetching job suggestions: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
            result.put("jobSuggestions", jobSuggestions);

        // --- Skill improvement tips keyed by skill ---
        Map<String, Map<String, String>> skillTipsMap = new java.util.LinkedHashMap<>();
        skillTipsMap.put("java",           Map.of("tip", "Practice Java collections, streams and concurrency.", "resource", "https://www.baeldung.com", "resourceLabel", "Baeldung Java Guides"));
        skillTipsMap.put("spring",         Map.of("tip", "Build REST APIs with Spring Boot and learn Spring Security.", "resource", "https://spring.io/guides", "resourceLabel", "Spring Official Guides"));
        skillTipsMap.put("spring boot",    Map.of("tip", "Build REST APIs with Spring Boot and learn Spring Security.", "resource", "https://spring.io/guides", "resourceLabel", "Spring Official Guides"));
        skillTipsMap.put("python",         Map.of("tip", "Master Python data structures, OOP and scripting.", "resource", "https://realpython.com", "resourceLabel", "Real Python"));
        skillTipsMap.put("machine learning",Map.of("tip", "Learn ML fundamentals: regression, classification, neural networks.", "resource", "https://www.coursera.org/learn/machine-learning", "resourceLabel", "Coursera ML by Andrew Ng"));
        skillTipsMap.put("deep learning",  Map.of("tip", "Study CNNs, RNNs and transformers with hands-on projects.", "resource", "https://www.deeplearning.ai", "resourceLabel", "DeepLearning.AI"));
        skillTipsMap.put("tensorflow",     Map.of("tip", "Build and train models using TensorFlow and Keras.", "resource", "https://www.tensorflow.org/tutorials", "resourceLabel", "TensorFlow Tutorials"));
        skillTipsMap.put("pytorch",        Map.of("tip", "Learn PyTorch for research-oriented deep learning.", "resource", "https://pytorch.org/tutorials", "resourceLabel", "PyTorch Tutorials"));
        skillTipsMap.put("javascript",     Map.of("tip", "Master ES6+, async/await, closures and DOM manipulation.", "resource", "https://javascript.info", "resourceLabel", "javascript.info"));
        skillTipsMap.put("typescript",     Map.of("tip", "Learn TypeScript types, interfaces, generics and decorators.", "resource", "https://www.typescriptlang.org/docs", "resourceLabel", "TypeScript Docs"));
        skillTipsMap.put("react",          Map.of("tip", "Master hooks, context API, and state management with Redux.", "resource", "https://react.dev", "resourceLabel", "React Official Docs"));
        skillTipsMap.put("angular",        Map.of("tip", "Learn Angular components, services, RxJS and lazy loading.", "resource", "https://angular.dev", "resourceLabel", "Angular Official Docs"));
        skillTipsMap.put("node",           Map.of("tip", "Build scalable APIs with Node.js, Express and async patterns.", "resource", "https://nodejs.org/en/learn", "resourceLabel", "Node.js Docs"));
        skillTipsMap.put("sql",            Map.of("tip", "Practice complex queries, joins, indexes and query optimisation.", "resource", "https://sqlzoo.net", "resourceLabel", "SQLZoo"));
        skillTipsMap.put("mongodb",        Map.of("tip", "Learn MongoDB aggregation pipelines and schema design.", "resource", "https://learn.mongodb.com", "resourceLabel", "MongoDB University"));
        skillTipsMap.put("aws",            Map.of("tip", "Get AWS Cloud Practitioner or Solutions Architect certified.", "resource", "https://aws.amazon.com/training", "resourceLabel", "AWS Training"));
        skillTipsMap.put("azure",          Map.of("tip", "Study Azure fundamentals and pursue AZ-900 certification.", "resource", "https://learn.microsoft.com/en-us/azure", "resourceLabel", "Microsoft Learn"));
        skillTipsMap.put("docker",         Map.of("tip", "Learn containerisation, Dockerfile best practices and Docker Compose.", "resource", "https://docs.docker.com/get-started", "resourceLabel", "Docker Get Started"));
        skillTipsMap.put("kubernetes",     Map.of("tip", "Study pods, deployments, services and Helm charts.", "resource", "https://kubernetes.io/docs/tutorials", "resourceLabel", "Kubernetes Tutorials"));
        skillTipsMap.put("devops",         Map.of("tip", "Learn CI/CD pipelines, infrastructure as code and monitoring.", "resource", "https://roadmap.sh/devops", "resourceLabel", "DevOps Roadmap"));
        skillTipsMap.put("git",            Map.of("tip", "Master branching strategies, rebasing and pull request workflows.", "resource", "https://learngitbranching.js.org", "resourceLabel", "Learn Git Branching"));
        skillTipsMap.put("kotlin",         Map.of("tip", "Learn Kotlin coroutines, data classes and Android development.", "resource", "https://kotlinlang.org/docs", "resourceLabel", "Kotlin Docs"));
        skillTipsMap.put("flutter",        Map.of("tip", "Build cross-platform apps with Flutter widgets and state management.", "resource", "https://flutter.dev/docs", "resourceLabel", "Flutter Docs"));
        skillTipsMap.put("graphql",        Map.of("tip", "Learn GraphQL queries, mutations, resolvers and Apollo.", "resource", "https://graphql.org/learn", "resourceLabel", "GraphQL Learn"));
        skillTipsMap.put("kafka",          Map.of("tip", "Study Kafka topics, partitions, consumers and stream processing.", "resource", "https://kafka.apache.org/documentation", "resourceLabel", "Kafka Docs"));

            List<Map<String, String>> skillTips = foundSkills.stream()
                    .filter(skillTipsMap::containsKey)
                    .limit(6)
                    .map(skillTipsMap::get)
                    .collect(java.util.stream.Collectors.toList());
            result.put("skillTips", skillTips);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process file: " + e.getClass().getSimpleName() + " - " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "An error occurred while analyzing resume",
                    "details", e.getClass().getSimpleName() + " - " + (e.getMessage() == null ? "No message available" : e.getMessage())
            ));
        }
    }

    private String extractText(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String filename = java.util.Objects.toString(file.getOriginalFilename(), "").toLowerCase();
        byte[] bytes = file.getBytes();
        if (bytes.length == 0) {
            return "";
        }
        try {
            if (filename.endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(bytes)) {
                    String text = new org.apache.pdfbox.text.PDFTextStripper().getText(doc);
                    return text != null ? text : "";
                }
            }
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error extracting text from file: " + e.getMessage());
            // Try to extract as plain text as fallback
            try {
                return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception ex) {
                return "";
            }
        }
    }
}
