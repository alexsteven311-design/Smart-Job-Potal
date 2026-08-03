package com.smartjobportal.repository;

import com.smartjobportal.model.MockInterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MockInterviewSessionRepository extends JpaRepository<MockInterviewSession, Long> {
    Optional<MockInterviewSession> findByIdAndCandidateId(Long id, Long candidateId);
    List<MockInterviewSession> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
}
