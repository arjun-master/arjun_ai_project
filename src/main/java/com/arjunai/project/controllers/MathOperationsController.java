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
@Tag(name = "Math Operations", description = "Basic mathematical operations API")
@RequiredArgsConstructor
public class MathOperationsController {

    private final ApiLogService apiLogService;

    /**
     * Adds two numbers with performance monitoring.
     *
     * @param a First number
     * @param b Second number
     * @return Sum of the two numbers
     */
    @Operation(summary = "Add two numbers")
    @GetMapping("/add")
    public ResponseEntity<Double> add(
            @Parameter(description = "First number") @RequestParam double a,
            @Parameter(description = "Second number") @RequestParam double b) {
        Instant startTime = Instant.now();
        String endpoint = "/api/math/add";
        Map<String, Double> request = Map.of("a", a, "b", b);
        ApiLog apiLog = apiLogService.startLog("add", endpoint, request);
        
        try {
            Double result = a + b;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Subtracts two numbers with performance monitoring.
     *
     * @param a First number
     * @param b Second number
     * @return Difference of the two numbers
     */
    @Operation(summary = "Subtract two numbers")
    @GetMapping("/subtract")
    public ResponseEntity<Double> subtract(
            @Parameter(description = "First number") @RequestParam double a,
            @Parameter(description = "Second number") @RequestParam double b) {
        Instant startTime = Instant.now();
        String endpoint = "/api/math/subtract";
        Map<String, Double> request = Map.of("a", a, "b", b);
        ApiLog apiLog = apiLogService.startLog("subtract", endpoint, request);
        
        try {
            Double result = a - b;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Multiplies two numbers with performance monitoring.
     *
     * @param a First number
     * @param b Second number
     * @return Product of the two numbers
     */
    @Operation(summary = "Multiply two numbers")
    @GetMapping("/multiply")
    public ResponseEntity<Double> multiply(
            @Parameter(description = "First number") @RequestParam double a,
            @Parameter(description = "Second number") @RequestParam double b) {
        Instant startTime = Instant.now();
        String endpoint = "/api/math/multiply";
        Map<String, Double> request = Map.of("a", a, "b", b);
        ApiLog apiLog = apiLogService.startLog("multiply", endpoint, request);
        
        try {
            Double result = a * b;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Divides two numbers with performance monitoring.
     *
     * @param a Dividend
     * @param b Divisor
     * @return Quotient of the division
     * @throws IllegalArgumentException if divisor is zero
     */
    @Operation(summary = "Divide two numbers")
    @GetMapping("/divide")
    public ResponseEntity<Double> divide(
            @Parameter(description = "Dividend") @RequestParam double a,
            @Parameter(description = "Divisor") @RequestParam double b) {
        Instant startTime = Instant.now();
        String endpoint = "/api/math/divide";
        Map<String, Double> request = Map.of("a", a, "b", b);
        ApiLog apiLog = apiLogService.startLog("divide", endpoint, request);
        
        try {
            if (b == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            Double result = a / b;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
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