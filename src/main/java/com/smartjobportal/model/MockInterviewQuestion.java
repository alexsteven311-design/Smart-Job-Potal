package com.smartjobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MockInterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private MockInterviewSession session;

    @Column(length = 2000, nullable = false)
    private String questionText;
    private String category;
    private Integer questionOrder;

    @Column(length = 12000)
    private String answerText;
    private Integer score;
    @Column(length = 4000)
    private String feedback;
    private Boolean aiFeedback;
    private LocalDateTime answeredAt;

    public Long getId() { return id; }
    public MockInterviewSession getSession() { return session; }
    public void setSession(MockInterviewSession session) { this.session = session; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Boolean getAiFeedback() { return aiFeedback; }
    public void setAiFeedback(Boolean aiFeedback) { this.aiFeedback = aiFeedback; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}
