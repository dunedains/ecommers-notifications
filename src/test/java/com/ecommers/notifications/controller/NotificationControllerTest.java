package com.ecommers.notifications.controller;

import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;
import com.ecommers.notifications.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService service;

    private NotificationResponse sample() {
        return new NotificationResponse(1L, 2L, "ORDER_CREATED", "msg", false, LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/notifications válido -> 201")
    void create_devuelve201() throws Exception {
        when(service.create(any())).thenReturn(sample());

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":2,\"type\":\"ORDER_CREATED\",\"message\":\"msg\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ORDER_CREATED"));
    }

    @Test
    @DisplayName("POST /api/notifications con tipo inválido -> 400")
    void create_tipoInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":2,\"type\":\"FOO\",\"message\":\"msg\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId} -> 200")
    void getByUser_devuelve200() throws Exception {
        when(service.getByUser(2L)).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/notifications/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{userId}/unread/count -> 200")
    void countUnread_devuelve200() throws Exception {
        when(service.countUnread(2L)).thenReturn(3L);

        mockMvc.perform(get("/api/notifications/user/2/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unread").value(3));
    }

    @Test
    @DisplayName("PATCH /api/notifications/{id}/read -> 200")
    void markAsRead_devuelve200() throws Exception {
        when(service.markAsRead(1L)).thenReturn(sample());

        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/notifications/{id} -> 204")
    void delete_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());
    }
}
