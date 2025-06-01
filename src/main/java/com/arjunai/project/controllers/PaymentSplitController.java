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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller for handling payment splitting operations.
 * Provides REST endpoints for various bill splitting scenarios.
 *
 * @author Arjun Raju
 * @version 1.2
 */
@Slf4j
@RestController
@RequestMapping("/api/split")
@Tag(name = "Payment Split", description = "Payment splitting operations API")
@RequiredArgsConstructor
public class PaymentSplitController {

    private final ApiLogService apiLogService;

    /**
     * Splits amount equally among people with performance monitoring.
     *
     * @param amount Total amount to split
     * @param people Number of people
     * @return Amount per person
     * @throws IllegalArgumentException if people count is invalid
     */
    @Operation(summary = "Split amount equally")
    @GetMapping("/equal")
    public ResponseEntity<Double> splitEqually(@RequestParam double amount, @RequestParam int people) {
        Instant startTime = Instant.now();
        String endpoint = "/api/split/equal";
        Map<String, Object> request = Map.of("amount", amount, "people", people);
        ApiLog apiLog = apiLogService.startLog("splitEqually", endpoint, request);
        
        try {
            if (people <= 0) {
                throw new IllegalArgumentException("Number of people must be greater than zero");
            }
            Double result = amount / people;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Splits amount with tip calculation and performance monitoring.
     *
     * @param amount Total amount to split
     * @param people Number of people
     * @param tipPercentage Tip percentage
     * @return Amount per person including tip
     * @throws IllegalArgumentException if input parameters are invalid
     */
    @Operation(summary = "Split amount with tip")
    @GetMapping("/with-tip")
    public ResponseEntity<Double> splitWithTip(@RequestParam double amount, @RequestParam int people, @RequestParam double tipPercentage) {
        Instant startTime = Instant.now();
        String endpoint = "/api/split/with-tip";
        Map<String, Object> request = Map.of("amount", amount, "people", people, "tipPercentage", tipPercentage);
        ApiLog apiLog = apiLogService.startLog("splitWithTip", endpoint, request);
        
        try {
            if (people <= 0) {
                throw new IllegalArgumentException("Number of people must be greater than zero");
            }
            double totalAmount = amount * (1 + tipPercentage / 100);
            Double result = totalAmount / people;
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Splits amount according to custom ratios with performance monitoring.
     *
     * @param amount Total amount to split
     * @param ratios Array of ratios for splitting
     * @return Array of split amounts
     * @throws IllegalArgumentException if input parameters are invalid
     */
    @Operation(summary = "Split amount with custom ratios")
    @PostMapping("/custom")
    public ResponseEntity<List<Double>> splitCustom(@RequestParam double amount, @RequestParam double[] ratios) {
        Instant startTime = Instant.now();
        String endpoint = "/api/split/custom";
        Map<String, Object> request = Map.of("amount", amount, "ratios", ratios);
        ApiLog apiLog = apiLogService.startLog("splitCustom", endpoint, request);
        
        try {
            if (ratios == null || ratios.length == 0) {
                throw new IllegalArgumentException("At least one ratio is required");
            }
            double totalRatio = Arrays.stream(ratios).sum();
            List<Double> shares = Arrays.stream(ratios)
                    .mapToObj(ratio -> (ratio / totalRatio) * amount)
                    .collect(Collectors.toList());
            apiLogService.completeLog(apiLog, shares, startTime);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }

    /**
     * Splits bill by items with performance monitoring.
     *
     * @param itemsRequest Map of items and their prices
     * @param participants List of participants
     * @return Map of participants and their shares
     * @throws IllegalArgumentException if input parameters are invalid
     */
    @Operation(summary = "Split bill by items")
    @PostMapping("/byItems")
    public ResponseEntity<Map<String, Double>> splitByItems(
            @RequestBody Map<String, Double> itemsRequest,
            @RequestParam String[] participants) {
        Instant startTime = Instant.now();
        String endpoint = "/api/split/byItems";
        Map<String, Object> request = new HashMap<>();
        request.put("items", itemsRequest);
        request.put("participants", participants);
        ApiLog apiLog = apiLogService.startLog("splitByItems", endpoint, request);
        
        try {
            if (participants == null || participants.length == 0) {
                throw new IllegalArgumentException("At least one participant is required");
            }
            if (itemsRequest == null || itemsRequest.isEmpty()) {
                throw new IllegalArgumentException("At least one item is required");
            }
            
            double total = itemsRequest.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            
            if (total <= 0) {
                throw new IllegalArgumentException("Total amount must be greater than zero");
            }
            
            double perPerson = total / participants.length;
            
            Map<String, Double> result = new HashMap<>();
            for (String participant : participants) {
                result.put(participant, perPerson);
            }
            
            apiLogService.completeLog(apiLog, result, startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            apiLogService.logError(apiLog, e, startTime);
            throw e;
        }
    }
}

@RestControllerAdvice
class PaymentSplitExceptionHandler {
    private final ApiLogService apiLogService;

    PaymentSplitExceptionHandler(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("body", e.getMessage());
        
        ApiLog apiLog = apiLogService.startLog("splitByItems", endpoint, requestData);
        apiLogService.logError(apiLog, e, Instant.now());
        
        return ResponseEntity.badRequest().body("Invalid JSON format");
    }
} 