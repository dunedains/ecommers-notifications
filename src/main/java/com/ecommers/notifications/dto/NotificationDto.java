package com.ecommers.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class NotificationDto {

    public record NotificationRequest(
            @NotNull(message = "userId es obligatorio")
            Long userId,

            @NotBlank(message = "El tipo es obligatorio")
            @Pattern(
                regexp = "ORDER_CREATED|ORDER_CONFIRMED|ORDER_CANCELLED|PAYMENT_COMPLETED|PAYMENT_FAILED|PAYMENT_REFUNDED",
                message = "Tipo inválido"
            )
            String type,

            @NotBlank(message = "El mensaje es obligatorio")
            String message
    ) {}

    public record NotificationResponse(
            Long id,
            Long userId,
            String type,
            String message,
            boolean read,
            LocalDateTime createdAt
    ) {}
}
