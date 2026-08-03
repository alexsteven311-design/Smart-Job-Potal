package com.smartjobportal.controller;

import com.smartjobportal.model.Post;
import com.smartjobportal.model.PostComment;
import com.smartjobportal.model.User;
import com.smartjobportal.repository.PostCommentRepository;
import com.smartjobportal.repository.PostRepository;
import com.smartjobportal.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository commentRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository,
                          PostCommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public List<Post> getAll(@RequestParam(name = "category", required = false) String category) {
        if (category != null && !category.isBlank()) {
            return postRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        }
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        post.setAuthor(user);
        if (post.getAuthorName() == null || post.getAuthorName().isBlank()) {
            post.setAuthorName(user.getName());
        }
        post.setCreatedAt(LocalDateTime.now());
        post.setLikes(0);
        post.setComments(0);
        return ResponseEntity.ok(postRepository.save(post));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Integer>> like(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
        return ResponseEntity.ok(Map.of("likes", post.getLikes()));
    }

    @GetMapping("/{id}/comments")
    public List<PostComment> getComments(@PathVariable Long id) {
        return commentRepository.findByPost_IdOrderByCreatedAtAsc(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<PostComment> addComment(@PathVariable Long id,
                                                  @RequestBody PostComment body,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        body.setPost(post);
        body.setAuthorName(user.getName());
        body.setCreatedAt(LocalDateTime.now());
        PostComment saved = commentRepository.save(body);
        post.setComments(post.getComments() + 1);
        postRepository.save(post);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Integer>> share(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setShares(post.getShares() + 1);
        postRepository.save(post);
        return ResponseEntity.ok(Map.of("shares", post.getShares()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if (!post.getAuthor().getId().equals(user.getId()) && !user.getRole().equals("admin")) {
            return ResponseEntity.status(403).build();
        }
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
