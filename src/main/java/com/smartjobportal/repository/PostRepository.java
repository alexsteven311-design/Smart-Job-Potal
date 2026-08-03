package com.smartjobportal.repository;

import com.smartjobportal.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);
}
