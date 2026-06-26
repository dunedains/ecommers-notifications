package com.ecommers.notifications.service.impl;

import com.ecommers.notifications.dto.NotificationDto.NotificationRequest;
import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;
import com.ecommers.notifications.exception.NotificationNotFoundException;
import com.ecommers.notifications.model.Notification;
import com.ecommers.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de notificaciones.
 * Se mockea el repositorio para aislar la lógica de la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    @DisplayName("create: persiste la notificación con sus datos")
    void create_persisteNotificacion() {
        when(repository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(1L);
            return n;
        });

        NotificationResponse response = service.create(
                new NotificationRequest(2L, "ORDER_CREATED", "Tu orden fue creada"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.type()).isEqualTo("ORDER_CREATED");
        assertThat(response.read()).isFalse();
        verify(repository).save(any(Notification.class));
    }

    @Test
    @DisplayName("markAsRead: marca como leída una notificación existente")
    void markAsRead_existente_marcaLeida() {
        Notification n = new Notification();
        n.setId(1L);
        n.setUserId(2L);
        n.setType("ORDER_CREATED");
        n.setMessage("msg");
        n.setRead(false);
        when(repository.findById(1L)).thenReturn(Optional.of(n));
        when(repository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        NotificationResponse response = service.markAsRead(1L);

        assertThat(response.read()).isTrue();
    }

    @Test
    @DisplayName("markAsRead: si no existe, lanza NotificationNotFoundException")
    void markAsRead_inexistente_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsRead(99L))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    @DisplayName("countUnread: devuelve el conteo del repositorio")
    void countUnread_devuelveConteo() {
        when(repository.countByUserIdAndReadFalse(2L)).thenReturn(3L);

        assertThat(service.countUnread(2L)).isEqualTo(3L);
    }

    @Test
    @DisplayName("delete: si no existe, lanza excepción y no borra")
    void delete_inexistente_lanzaExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotificationNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: borra una notificación existente")
    void delete_existente_borra() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("getByUser / getUnreadByUser: devuelven las notificaciones del usuario")
    void getByUserYUnread_devuelvenListas() {
        Notification n = new Notification();
        n.setId(1L);
        n.setUserId(2L);
        n.setType("ORDER_CREATED");
        n.setMessage("msg");
        when(repository.findByUserIdOrderByCreatedAtDesc(2L)).thenReturn(java.util.List.of(n));
        when(repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(2L)).thenReturn(java.util.List.of(n));

        assertThat(service.getByUser(2L)).hasSize(1);
        assertThat(service.getUnreadByUser(2L)).hasSize(1);
    }

    @Test
    @DisplayName("markAllAsRead: marca como leídas todas las no leídas del usuario")
    void markAllAsRead_marcaTodas() {
        Notification n = new Notification();
        n.setId(1L);
        n.setUserId(2L);
        n.setType("ORDER_CREATED");
        n.setMessage("msg");
        n.setRead(false);
        when(repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(2L)).thenReturn(java.util.List.of(n));

        service.markAllAsRead(2L);

        assertThat(n.isRead()).isTrue();
        verify(repository).save(n);
    }
}
