package com.arjunai.project.models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLog {
    private Long id;
    private String methodName;
    private String endpoint;
    private String request;
    private String response;
    private Instant requestTime;
    private Instant responseTime;
    private Long executionTimeMs;
    private String errorMessage;
    private boolean successful;
} 