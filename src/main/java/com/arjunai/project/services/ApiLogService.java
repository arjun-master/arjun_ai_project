package com.arjunai.project.services;

import com.arjunai.project.models.ApiLog;
import com.arjunai.project.repositories.ApiLogRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "apiLogs")
public class ApiLogService {
    private final ApiLogRepository apiLogRepository;

    @CircuitBreaker(name = "apiLogService", fallbackMethod = "fallbackStartLog")
    @Retry(name = "apiLogService")
    public ApiLog startLog(String methodName, String endpoint, Map<String, Object> request) {
        ApiLog apiLog = new ApiLog();
        apiLog.setMethodName(methodName);
        apiLog.setEndpoint(endpoint);
        apiLog.setRequest(request.toString());
        apiLog.setRequestTime(Instant.now());
        return apiLogRepository.save(apiLog);
    }

    @CircuitBreaker(name = "apiLogService", fallbackMethod = "fallbackCompleteLog")
    @Retry(name = "apiLogService")
    @Transactional
    public void completeLog(ApiLog apiLog, Object response, Instant startTime) {
        apiLog.setResponse(response.toString());
        apiLog.setResponseTime(Instant.now());
        apiLog.setExecutionTimeMs(apiLog.getResponseTime().toEpochMilli() - startTime.toEpochMilli());
        apiLog.setSuccessful(true);
        apiLogRepository.save(apiLog);
    }

    @CircuitBreaker(name = "apiLogService", fallbackMethod = "fallbackLogError")
    @Retry(name = "apiLogService")
    @Transactional
    public void logError(ApiLog apiLog, Exception e, Instant startTime) {
        apiLog.setErrorMessage(e.getMessage());
        apiLog.setResponseTime(Instant.now());
        apiLog.setExecutionTimeMs(apiLog.getResponseTime().toEpochMilli() - startTime.toEpochMilli());
        apiLog.setSuccessful(false);
        apiLogRepository.save(apiLog);
    }

    @Cacheable(key = "#methodName")
    public double getAverageExecutionTime(String methodName) {
        return apiLogRepository.findAverageExecutionTimeByMethodName(methodName);
    }

    // Fallback methods
    private ApiLog fallbackStartLog(String methodName, String endpoint, Map<String, Object> request, Exception e) {
        log.error("Circuit breaker fallback: Failed to start log", e);
        ApiLog fallbackLog = new ApiLog();
        fallbackLog.setMethodName(methodName);
        fallbackLog.setEndpoint(endpoint);
        fallbackLog.setRequest("Fallback: " + request.toString());
        fallbackLog.setRequestTime(Instant.now());
        return fallbackLog;
    }

    private void fallbackCompleteLog(ApiLog apiLog, Object response, Instant startTime, Exception e) {
        log.error("Circuit breaker fallback: Failed to complete log", e);
    }

    private void fallbackLogError(ApiLog apiLog, Exception error, Instant startTime, Exception e) {
        log.error("Circuit breaker fallback: Failed to log error", e);
    }
} 