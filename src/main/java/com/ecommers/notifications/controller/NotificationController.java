package com.ecommers.notifications.controller;

import com.ecommers.notifications.dto.NotificationDto.NotificationRequest;
import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;
import com.ecommers.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<EntityModel<NotificationResponse>> create(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(service.create(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<NotificationResponse>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(toCollection(service.getByUser(userId), userId,
                linkTo(methodOn(NotificationController.class).getByUser(userId)).withSelfRel()));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<CollectionModel<EntityModel<NotificationResponse>>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(toCollection(service.getUnreadByUser(userId), userId,
                linkTo(methodOn(NotificationController.class).getUnread(userId)).withSelfRel()));
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("unread", service.countUnread(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<EntityModel<NotificationResponse>> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(toModel(service.markAsRead(id)));
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

    private CollectionModel<EntityModel<NotificationResponse>> toCollection(
            List<NotificationResponse> list, Long userId, org.springframework.hateoas.Link selfLink) {
        List<EntityModel<NotificationResponse>> models = list.stream().map(this::toModel).toList();
        return CollectionModel.of(models)
                .add(selfLink)
                .add(linkTo(methodOn(NotificationController.class).countUnread(userId)).withRel("unread-count"));
    }

    private EntityModel<NotificationResponse> toModel(NotificationResponse n) {
        return EntityModel.of(n,
                linkTo(methodOn(NotificationController.class).getByUser(n.userId())).withRel("user-notifications"));
    }
}
