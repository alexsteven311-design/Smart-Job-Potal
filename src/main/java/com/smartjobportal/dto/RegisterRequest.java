package com.smartjobportal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String role;
    private String company;
    private String phoneNumber;
    private String location;
    private String linkedInProfileUrl;
    private String githubPortfolioUrl;
    private String portfolioWebsite;
    private String skills;
    private Integer experienceYears;
    private String preferredRole;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getLinkedInProfileUrl() { return linkedInProfileUrl; }
    public void setLinkedInProfileUrl(String linkedInProfileUrl) { this.linkedInProfileUrl = linkedInProfileUrl; }

    public String getGithubPortfolioUrl() { return githubPortfolioUrl; }
    public void setGithubPortfolioUrl(String githubPortfolioUrl) { this.githubPortfolioUrl = githubPortfolioUrl; }

    public String getPortfolioWebsite() { return portfolioWebsite; }
    public void setPortfolioWebsite(String portfolioWebsite) { this.portfolioWebsite = portfolioWebsite; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getPreferredRole() { return preferredRole; }
    public void setPreferredRole(String preferredRole) { this.preferredRole = preferredRole; }
}
