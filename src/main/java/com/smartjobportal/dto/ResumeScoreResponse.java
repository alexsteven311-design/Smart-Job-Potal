package com.smartjobportal.dto;

import java.util.List;
import java.util.Map;

public class ResumeScoreResponse {
    private Long jobId;
    private double matchPercentage;
    private int requiredSkillCount;
    private int matchedCount;
    private List<String> matchedSkills;
    private Map<String, Object> details;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getRequiredSkillCount() {
        return requiredSkillCount;
    }

    public void setRequiredSkillCount(int requiredSkillCount) {
        this.requiredSkillCount = requiredSkillCount;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
