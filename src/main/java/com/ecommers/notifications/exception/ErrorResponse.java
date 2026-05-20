package com.ecommers.notifications.exception;

public record ErrorResponse(int status, String message, String timestamp) {}
