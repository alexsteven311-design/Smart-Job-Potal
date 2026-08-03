package com.smartjobportal.repository;

import com.smartjobportal.model.MockInterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MockInterviewQuestionRepository extends JpaRepository<MockInterviewQuestion, Long> {
    Optional<MockInterviewQuestion> findByIdAndSessionCandidateId(Long id, Long candidateId);
}
