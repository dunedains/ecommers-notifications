package com.ecommers.notifications.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.ecommers.notifications.dto.NotificationDto.NotificationRequest;
import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;
import com.ecommers.notifications.exception.NotificationNotFoundException;
import com.ecommers.notifications.model.Notification;
import com.ecommers.notifications.repository.NotificationRepository;
import com.ecommers.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        log.info("Creando notificación type={} userId={}", request.type(), request.userId());
        Notification notification = new Notification();
        notification.setUserId(request.userId());
        notification.setType(request.type());
        notification.setMessage(request.message());
        return toResponse(repository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUser(Long userId) {
        log.info("Obteniendo notificaciones userId={}", userId);
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadByUser(Long userId) {
        log.info("Obteniendo no leídas userId={}", userId);
        return repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        log.info("Marcando como leída notificación id={}", id);
        Notification notification = findOrThrow(id);
        notification.setRead(true);
        return toResponse(repository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marcando todas como leídas userId={}", userId);
        repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .forEach(n -> {
                    n.setRead(true);
                    repository.save(n);
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando notificación id={}", id);
        if (!repository.existsById(id)) {
            throw new NotificationNotFoundException("Notificación no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Notification findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notificación no encontrada con id: " + id));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.getType(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
