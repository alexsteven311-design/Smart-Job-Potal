package com.smartjobportal.service;

import com.smartjobportal.model.User;
import com.smartjobportal.dto.ResumeParseResponse;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final Path uploadPath;
    private final UserService userService;

    public ResumeService(@Value("${resume.upload.dir}") String uploadDir, UserService userService) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.userService = userService;
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + this.uploadPath, e);
        }
    }

    public User storeResume(User user, MultipartFile file) throws IOException {
        Files.createDirectories(uploadPath);
        String filename = user.getId() + "_" + file.getOriginalFilename();
        Path target = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        ResumeParseResponse parsed = parseResumeFile(file);
        if (parsed.getSkills() != null && !parsed.getSkills().isEmpty()) {
            user.setSkills(String.join(",", parsed.getSkills()));
        }

        double score = computeResumeScore(user.getSkills(), file.getSize());
        user.setResumeFilename(filename);
        user.setResumeScore(score);
        return userService.saveUser(user);
    }

    public ResumeParseResponse parseResumeFile(MultipartFile file) throws IOException {
        String text = extractTextFromFile(file);
        return parseResumeText(text);
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        String filename = Objects.toString(file.getOriginalFilename(), "").toLowerCase();
        if (filename.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                return new PDFTextStripper().getText(document);
            }
        }
        if (filename.endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        try (Scanner scanner = new Scanner(file.getInputStream(), StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public String getUploadDir() {
        return uploadPath.toString();
    }

    public double computeResumeScore(String skills, long fileSize) {
        List<String> skillList = skills == null ? List.of() : List.of(skills.split(","));
        double score = 40;
        score += Math.min(35, skillList.stream().filter(s -> !s.isBlank()).count() * 5);
        score += Math.min(25, fileSize / 100_000.0);
        return Math.min(100, score);
    }

    public ResumeParseResponse parseResumeText(String resumeText) {
        ResumeParseResponse response = new ResumeParseResponse();
        if (resumeText == null || resumeText.isBlank()) {
            response.setName("");
            response.setEmail("");
            response.setSkills(List.of());
            response.setEducation(List.of());
            response.setExperience(List.of());
            return response;
        }

        try {
            String normalized = resumeText.replace("\r\n", "\n").trim();
            List<String> lines = normalized.lines().map(String::trim).filter(line -> !line.isBlank()).collect(Collectors.toList());

            String email = extractEmail(normalized);
            response.setEmail(email != null ? email : "");
            response.setName(extractName(lines, response.getEmail()));
            
            List<String> skills = extractSectionItems(normalized, List.of("skills", "technical skills", "core competencies", "skillset"));
            response.setSkills(skills != null ? skills : List.of());
            
            List<String> education = extractSectionItems(normalized, List.of("education", "academic qualifications", "education & training"));
            response.setEducation(education != null ? education : List.of());
            
            List<String> experience = extractSectionItems(normalized, List.of("experience", "work experience", "professional experience", "employment history"));
            response.setExperience(experience != null ? experience : List.of());
        } catch (Exception e) {
            System.err.println("Error parsing resume text: " + e.getMessage());
            response.setName("");
            response.setEmail("");
            response.setSkills(List.of());
            response.setEducation(List.of());
            response.setExperience(List.of());
        }

        return response;
    }

    private String extractEmail(String text) {
        Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
        Matcher matcher = emailPattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }

    private String extractName(List<String> lines, String email) {
        for (String line : lines) {
            if (line.toLowerCase().contains("name")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && !parts[1].isBlank()) {
                    return parts[1].trim();
                }
            }
        }
        if (!email.isBlank()) {
            int index = lines.indexOf(email);
            if (index > 0) {
                return lines.get(index - 1);
            }
        }
        if (!lines.isEmpty()) {
            String firstLine = lines.get(0);
            if (firstLine.split(" ").length <= 4) {
                return firstLine;
            }
        }
        return "";
    }

    private List<String> extractSectionItems(String text, List<String> sectionTitles) {
        if (text == null || text.isBlank() || sectionTitles == null || sectionTitles.isEmpty()) {
            return List.of();
        }
        try {
            String lower = text.toLowerCase();
            List<String> results = new ArrayList<>();
            for (String title : sectionTitles) {
                if (title == null || title.isBlank()) continue;
                int start = lower.indexOf(title);
                if (start >= 0) {
                    int end = findNextSectionStart(lower, start + title.length());
                    if (end >= start + title.length() && end <= text.length()) {
                        String section = text.substring(start + title.length(), end).trim();
                        List<String> lines = parseSectionLines(section);
                        if (lines != null) {
                            results.addAll(lines);
                        }
                    }
                }
            }
            return results.stream().distinct().collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error extracting section items: " + e.getMessage());
            return List.of();
        }
    }

    private int findNextSectionStart(String lowerText, int start) {
        if (lowerText == null || start < 0 || start > lowerText.length()) {
            return lowerText != null ? lowerText.length() : 0;
        }
        List<String> headings = List.of("skills", "education", "experience", "work experience", "professional experience", "employment history", "projects", "certifications", "summary", "objective", "contact");
        int nextStart = lowerText.length();
        for (String heading : headings) {
            if (heading == null || heading.length() == 0) continue;
            try {
                int idx = lowerText.indexOf(heading, start);
                if (idx > start && idx < nextStart) {
                    nextStart = idx;
                }
            } catch (Exception e) {
                // Skip this heading on error
            }
        }
        return nextStart;
    }

    private List<String> parseSectionLines(String section) {
        if (section == null || section.isBlank()) {
            return List.of();
        }
        try {
            return section.lines()
                    .map(String::trim)
                    .filter(line -> line != null && !line.isBlank())
                    .filter(line -> line.length() > 2)
                    .map(line -> line.replaceAll("^[•\\-*\\s]+", ""))
                    .filter(line -> line.length() > 2)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error parsing section lines: " + e.getMessage());
            return List.of();
        }
    }
}
