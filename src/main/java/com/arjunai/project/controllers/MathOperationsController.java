package com.arjunai.project.controllers;

import com.arjunai.project.models.ApiLog;
import com.arjunai.project.services.ApiLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for handling mathematical operations.
 * Provides REST endpoints for basic arithmetic operations.
 *
 * @author Arjun Raju
 * @version 1.2
 */
@Slf4j
@RestController
@RequestMapping("/api/math")
@Tag(name = "Math Operations", description = "API endpoints for basic mathematical operations")
@RequiredArgsConstructor
public class MathOperationsController {

    private final ApiLogService apiLogService;

    @PostMapping("/add")
    @Operation(summary = "Add two numbers")
    public Map<String, Object> add(@RequestBody Map<String, Double> request) {
        var startTime = Instant.now();
        var apiLog = apiLogService.startLog("add", "/api/math/add", request);

        try {
            double num1 = request.get("num1");
            double num2 = request.get("num2");
            double result = num1 + num2;

            Map<String, Object> response = new HashMap<>();
            response.put("result", result);

            apiLogService.completeLog(apiLog, response, startTime);
            return response;
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two numbers")
    public Map<String, Object> subtract(@RequestBody Map<String, Double> request) {
        var startTime = Instant.now();
        var apiLog = apiLogService.startLog("subtract", "/api/math/subtract", new HashMap<>(request));

        try {
            double num1 = request.get("num1");
            double num2 = request.get("num2");
            double result = num1 - num2;

            Map<String, Object> response = new HashMap<>();
            response.put("result", result);

            apiLogService.completeLog(apiLog, response, startTime);
            return response;
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    @PostMapping("/multiply")
    @Operation(summary = "Multiply two numbers")
    public Map<String, Object> multiply(@RequestBody Map<String, Double> request) {
        var startTime = Instant.now();
        var apiLog = apiLogService.startLog("multiply", "/api/math/multiply", new HashMap<>(request));

        try {
            double num1 = request.get("num1");
            double num2 = request.get("num2");
            double result = num1 * num2;

            Map<String, Object> response = new HashMap<>();
            response.put("result", result);

            apiLogService.completeLog(apiLog, response, startTime);
            return response;
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two numbers")
    public Map<String, Object> divide(@RequestBody Map<String, Double> request) {
        var startTime = Instant.now();
        var apiLog = apiLogService.startLog("divide", "/api/math/divide", new HashMap<>(request));

        try {
            double num1 = request.get("num1");
            double num2 = request.get("num2");

            if (num2 == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }

            double result = num1 / num2;

            Map<String, Object> response = new HashMap<>();
            response.put("result", result);

            apiLogService.completeLog(apiLog, response, startTime);
            return response;
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }
}

@RestControllerAdvice
class MathOperationsExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
} 