package com.smartjobportal.dto;

public class ResumeScoreRequest {
    private Long jobId;
    private String skills; // comma-separated skills from resume

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
