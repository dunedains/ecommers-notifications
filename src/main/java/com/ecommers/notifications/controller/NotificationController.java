package com.ecommers.notifications.controller;

import com.ecommers.notifications.dto.NotificationDto.NotificationRequest;
import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;
import com.ecommers.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnreadByUser(userId));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("unread", service.countUnread(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        service.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
