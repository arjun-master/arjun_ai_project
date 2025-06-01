package com.arjunai.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "api_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method_name")
    private String methodName;

    private String endpoint;
    private String request;
    private String response;

    @Column(name = "request_time")
    private Instant requestTime;

    @Column(name = "response_time")
    private Instant responseTime;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "error_message")
    private String errorMessage;

    private boolean successful;
} 