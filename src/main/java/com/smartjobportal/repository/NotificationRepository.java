package com.smartjobportal.repository;

import com.smartjobportal.model.Notification;
import com.smartjobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    long countByUserAndReadFalse(User user);
    boolean existsByUserAndTypeAndCreatedAtAfter(User user, String type, LocalDateTime after);
}
