package com.smartjobportal.controller;

import com.smartjobportal.model.Notification;
import com.smartjobportal.model.User;
import com.smartjobportal.service.NotificationService;
import com.smartjobportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAll(@AuthenticationPrincipal UserDetails principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(notificationService.getForUser(user));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user)));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal UserDetails principal) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.notFound().build();
        notificationService.markAllRead(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = resolveUser(principal);
        if (user == null) return ResponseEntity.notFound().build();
        notificationService.markRead(id, user);
        return ResponseEntity.ok().build();
    }

    private User resolveUser(UserDetails principal) {
        return userService.findByEmail(principal.getUsername()).orElse(null);
    }
}
