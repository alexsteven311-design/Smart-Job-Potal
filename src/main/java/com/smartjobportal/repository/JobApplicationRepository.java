package com.smartjobportal.repository;

import com.smartjobportal.model.JobApplication;
import com.smartjobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCandidateOrderByIdDesc(User candidate);
    List<JobApplication> findByJobId(Long jobId);
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
}
