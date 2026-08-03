package com.smartjobportal.repository;

import com.smartjobportal.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPost_IdOrderByCreatedAtAsc(Long postId);
}
