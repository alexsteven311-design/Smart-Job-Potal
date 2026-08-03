package com.smartjobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MockInterviewAnswerRequest {
    @NotBlank(message = "An answer is required")
    @Size(max = 12000)
    private String answer;
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
