package com.hexagonal.demo.infrastructure.config.error;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String code;
    private String message;
    private Integer status;
    private String path;
    private LocalDateTime timestamp;
}