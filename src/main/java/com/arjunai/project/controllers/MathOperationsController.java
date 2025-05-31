package com.arjunai.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling mathematical operations.
 * Provides REST endpoints for basic arithmetic operations.
 *
 * @author Arjun Raju
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/math")
@Tag(name = "Math Operations", description = "Basic mathematical operations API")
public class MathOperationsController {

    /**
     * Adds two numbers.
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
        log.debug("Adding {} and {}", a, b);
        return ResponseEntity.ok(a + b);
    }

    /**
     * Subtracts two numbers.
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
        log.debug("Subtracting {} from {}", b, a);
        return ResponseEntity.ok(a - b);
    }

    /**
     * Multiplies two numbers.
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
        log.debug("Multiplying {} and {}", a, b);
        return ResponseEntity.ok(a * b);
    }

    /**
     * Divides two numbers.
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
        log.debug("Dividing {} by {}", a, b);
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return ResponseEntity.ok(a / b);
    }
} 