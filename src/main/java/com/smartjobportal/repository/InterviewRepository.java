package com.smartjobportal.repository;

import com.smartjobportal.model.Interview;
import com.smartjobportal.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    @Query("SELECT i FROM Interview i WHERE i.application.candidate.id = :candidateId AND i.scheduledAt >= :now ORDER BY i.scheduledAt ASC")
    List<Interview> findUpcomingByCandidateId(Long candidateId, LocalDateTime now);
}
