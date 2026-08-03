package com.smartjobportal.service;

import com.smartjobportal.dto.MockInterviewStartRequest;
import com.smartjobportal.model.MockInterviewQuestion;
import com.smartjobportal.model.MockInterviewSession;
import com.smartjobportal.model.Job;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.JobRepository;
import com.smartjobportal.repository.MockInterviewQuestionRepository;
import com.smartjobportal.repository.MockInterviewSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class MockInterviewService {
    private final MockInterviewSessionRepository sessionRepository;
    private final MockInterviewQuestionRepository questionRepository;
    private final JobRepository jobRepository;
    private final AiFeedbackService aiFeedbackService;

    public MockInterviewService(MockInterviewSessionRepository sessionRepository, MockInterviewQuestionRepository questionRepository, JobRepository jobRepository, AiFeedbackService aiFeedbackService) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.jobRepository = jobRepository;
        this.aiFeedbackService = aiFeedbackService;
    }

    public Map<String, Object> start(User candidate, MockInterviewStartRequest request) {
        Job job = request.getJobId() == null ? null : jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new NoSuchElementException("Job not found"));
        String role = job == null
                ? nonBlank(request.getTargetRole(), candidate.getPreferredRole(), "the role you are targeting")
                : nonBlank(job.getRole(), job.getTitle(), "the role you are targeting");
        String difficulty = Optional.ofNullable(request.getDifficulty()).orElse("MEDIUM").trim().toUpperCase(Locale.ROOT);
        if (!Set.of("EASY", "MEDIUM", "HARD").contains(difficulty)) throw new IllegalArgumentException("Difficulty must be EASY, MEDIUM, or HARD");
        MockInterviewSession session = new MockInterviewSession();
        session.setCandidate(candidate); session.setTargetRole(role); session.setDifficulty(difficulty);
        if (job != null) {
            session.setJobId(job.getId());
            session.setJobTitle(job.getTitle() + " at " + job.getCompany());
            session.setJobDescription(job.getDescription());
        }
        List<String[]> prompts = promptSet(role, candidate.getSkills(), difficulty, job == null ? null : job.getDescription());
        int count = Math.min(request.getQuestionCount(), prompts.size());
        for (int i = 0; i < count; i++) {
            MockInterviewQuestion question = new MockInterviewQuestion();
            question.setSession(session); question.setCategory(prompts.get(i)[0]);
            question.setQuestionText(prompts.get(i)[1]); question.setQuestionOrder(i + 1);
            session.getQuestions().add(question);
        }
        return sessionView(sessionRepository.save(session), true);
    }

    public Map<String, Object> submitAnswer(User candidate, Long questionId, String answer) {
        MockInterviewQuestion question = questionRepository.findByIdAndSessionCandidateId(questionId, candidate.getId())
                .orElseThrow(() -> new NoSuchElementException("Interview question not found"));
        if (!"IN_PROGRESS".equals(question.getSession().getStatus())) throw new IllegalArgumentException("This mock interview is already complete");
        String cleanAnswer = answer.trim();
        Score score = scoreWithAi(question, cleanAnswer, question.getSession().getTargetRole(), candidate.getSkills());
        question.setAnswerText(cleanAnswer); question.setScore(score.value()); question.setFeedback(score.feedback()); question.setAnsweredAt(LocalDateTime.now());
        questionRepository.save(question);
        return questionView(question, true);
    }

    public Map<String, Object> complete(User candidate, Long sessionId) {
        MockInterviewSession session = ownedSession(candidate, sessionId);
        if (!"COMPLETED".equals(session.getStatus())) {
            List<MockInterviewQuestion> answered = session.getQuestions().stream().filter(q -> q.getScore() != null).toList();
            if (answered.isEmpty()) throw new IllegalArgumentException("Answer at least one question before completing the interview");
            int overall = (int) Math.round(answered.stream().mapToInt(MockInterviewQuestion::getScore).average().orElse(0));
            session.setOverallScore(overall); session.setStatus("COMPLETED"); session.setCompletedAt(LocalDateTime.now()); sessionRepository.save(session);
        }
        return sessionView(session, true);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(User candidate, Long sessionId) { return sessionView(ownedSession(candidate, sessionId), true); }
    @Transactional(readOnly = true)
    public List<Map<String, Object>> history(User candidate) { return sessionRepository.findByCandidateIdOrderByCreatedAtDesc(candidate.getId()).stream().map(s -> sessionView(s, false)).toList(); }

    private MockInterviewSession ownedSession(User candidate, Long sessionId) {
        return sessionRepository.findByIdAndCandidateId(sessionId, candidate.getId()).orElseThrow(() -> new NoSuchElementException("Mock interview not found"));
    }
    private String nonBlank(String first, String second, String fallback) { return first != null && !first.isBlank() ? first.trim() : second != null && !second.isBlank() ? second.trim() : fallback; }
    private List<String[]> promptSet(String role, String skills, String difficulty, String jobDescription) {
        String skill = skills == null || skills.isBlank() ? "a key skill for this role" : skills.split(",")[0].trim();
        String depth = "HARD".equals(difficulty) ? "including trade-offs and how you measured the result" : "and the outcome";
        if (jobDescription != null && !jobDescription.isBlank()) {
            List<String> requirements = extractRequirements(jobDescription);
            String primaryRequirement = requirements.isEmpty() ? skill : requirements.get(0);
            String secondaryRequirement = requirements.size() > 1 ? requirements.get(1) : "the responsibilities in this job description";
            return List.of(
                    new String[]{"INTRODUCTION", "This " + role + " opportunity involves " + primaryRequirement + ". What makes you a strong fit for it?"},
                    new String[]{"JOB_REQUIREMENT", "Describe a project where you demonstrated " + primaryRequirement + ". Explain your contribution " + depth + "."},
                    new String[]{"ROLE_KNOWLEDGE", "How would you approach " + secondaryRequirement + " in your first few weeks in this role?"},
                    new String[]{"PROBLEM_SOLVING", "The job description emphasises " + primaryRequirement + ". Tell me about a difficult problem in this area and how you solved it."},
                    new String[]{"COLLABORATION", "Give an example of working with stakeholders or teammates to deliver work similar to this role's responsibilities."},
                    new String[]{"IMPACT", "Which metric or outcome would you use to show success in this " + role + " role, and how have you improved something similar before?"},
                    new String[]{"GROWTH", "What would you need to learn to be effective in this role, and what is your plan for learning it?"},
                    new String[]{"CLOSING", "Based on this job description, why should we choose you for this opportunity?"}
            );
        }
        return List.of(
                new String[]{"INTRODUCTION", "Tell me about yourself and why you are interested in " + role + "."},
                new String[]{"EXPERIENCE", "Describe a project where you used " + skill + ". Explain your contribution " + depth + "."},
                new String[]{"PROBLEM_SOLVING", "Tell me about a challenging problem you faced in a project. How did you analyse it and what was the result?"},
                new String[]{"COLLABORATION", "Describe a time you received difficult feedback or disagreed with a teammate. What did you do?"},
                new String[]{"ROLE_KNOWLEDGE", "What makes someone effective as a " + role + ", and how are you developing those qualities?"},
                new String[]{"IMPACT", "Give an example of how you improved quality, speed, cost, or user experience in your work."},
                new String[]{"GROWTH", "What would you aim to learn in your first 90 days as a " + role + "?"},
                new String[]{"CLOSING", "Why should we choose you for this " + role + " opportunity?"}
        );
    }
    private List<String> extractRequirements(String description) {
        return Arrays.stream(description.replaceAll("[\\r\\n]+", ". ").split("[.;]"))
                .map(String::trim)
                .filter(text -> text.length() >= 12)
                .filter(text -> text.toLowerCase(Locale.ROOT).matches(".*(develop|build|design|manage|create|analyse|analyze|lead|implement|maintain|work|experience|knowledge|skill).*"))
                .limit(3)
                .toList();
    }
    private Score scoreWithAi(MockInterviewQuestion question, String answer, String role, String skills) {
        AiFeedbackService.AiScore aiScore = aiFeedbackService.score(
                question.getQuestionText(), question.getCategory(), answer, role);
        if (aiScore != null) {
            question.setAiFeedback(true);
            return new Score(aiScore.score(), aiScore.feedback());
        }
        question.setAiFeedback(false);
        return score(question, answer, role, skills);
    }
    private Score score(MockInterviewQuestion question, String answer, String role, String skills) {
        String lower = answer.toLowerCase(Locale.ROOT);
        int words = answer.isBlank() ? 0 : answer.trim().split("\\s+").length;
        int score = Math.min(30, words / 4); // clear, sufficiently developed answer
        boolean structured = lower.contains("situation") || lower.contains("challenge") || lower.contains("task") || lower.contains("action") || lower.contains("result");
        if (structured) score += 25;
        boolean impact = lower.matches("(?s).*\\b(impact|improved|increased|reduced|saved|delivered|result|%|percent)\\b.*");
        if (impact) score += 20;
        boolean relevant = lower.contains(role.toLowerCase(Locale.ROOT)) || (skills != null && Arrays.stream(skills.toLowerCase(Locale.ROOT).split(",")).map(String::trim).anyMatch(s -> s.length() > 2 && lower.contains(s)));
        if (relevant) score += 15;
        if (words >= 40) score += 10;
        score = Math.min(100, score);
        List<String> tips = new ArrayList<>();
        if (words < 40) tips.add("Add concrete detail: context, your specific actions, and the result.");
        if (!structured) tips.add("Use a STAR structure (Situation, Task, Action, Result) to make the answer easy to follow.");
        if (!impact) tips.add("Include a measurable outcome or clear impact where possible.");
        if (!relevant) tips.add("Connect the example directly to the target role or relevant skills.");
        String feedback = tips.isEmpty() ? "Strong answer: it is detailed, structured, relevant, and demonstrates impact." : String.join(" ", tips);
        return new Score(score, feedback);
    }
    private Map<String, Object> sessionView(MockInterviewSession session, boolean includeQuestions) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", session.getId()); view.put("targetRole", session.getTargetRole()); view.put("jobId", session.getJobId()); view.put("jobTitle", session.getJobTitle()); view.put("difficulty", session.getDifficulty()); view.put("status", session.getStatus());
        view.put("overallScore", session.getOverallScore()); view.put("createdAt", session.getCreatedAt()); view.put("completedAt", session.getCompletedAt());
        if (includeQuestions) view.put("questions", session.getQuestions().stream().map(q -> questionView(q, true)).toList());
        else { view.put("questionCount", session.getQuestions().size()); view.put("answeredCount", session.getQuestions().stream().filter(q -> q.getScore() != null).count()); }
        return view;
    }
    private Map<String, Object> questionView(MockInterviewQuestion q, boolean includeAnswer) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", q.getId()); view.put("order", q.getQuestionOrder()); view.put("category", q.getCategory()); view.put("question", q.getQuestionText()); view.put("score", q.getScore()); view.put("feedback", q.getFeedback()); view.put("aiFeedback", q.getAiFeedback()); view.put("answeredAt", q.getAnsweredAt());
        if (includeAnswer) view.put("answer", q.getAnswerText()); return view;
    }
    private record Score(int value, String feedback) { }
}
