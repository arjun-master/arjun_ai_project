package com.arjunai.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/split")
@Tag(name = "Payment Split", description = "Payment splitting operations API")
public class PaymentSplitController {

    @Operation(summary = "Split amount equally")
    @GetMapping("/equal")
    public ResponseEntity<Double> splitEqually(
            @Parameter(description = "Total amount") @RequestParam double amount,
            @Parameter(description = "Number of people") @RequestParam int people) {
        log.debug("Splitting {} among {} people", amount, people);
        if (people <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than zero");
        }
        return ResponseEntity.ok(amount / people);
    }

    @Operation(summary = "Split amount with tip")
    @GetMapping("/with-tip")
    public ResponseEntity<Double> splitWithTip(
            @Parameter(description = "Total amount") @RequestParam double amount,
            @Parameter(description = "Number of people") @RequestParam int people,
            @Parameter(description = "Tip percentage") @RequestParam double tipPercentage) {
        log.debug("Splitting {} among {} people with {}% tip", amount, people, tipPercentage);
        if (people <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than zero");
        }
        double totalAmount = amount * (1 + tipPercentage / 100);
        return ResponseEntity.ok(totalAmount / people);
    }

    @Operation(summary = "Split amount with custom ratios")
    @PostMapping("/custom")
    public ResponseEntity<double[]> splitCustom(
            @Parameter(description = "Total amount") @RequestParam double amount,
            @Parameter(description = "Split ratios") @RequestParam double[] ratios) {
        log.debug("Splitting {} with custom ratios", amount);
        double sum = 0;
        for (double ratio : ratios) {
            sum += ratio;
        }
        
        double[] shares = new double[ratios.length];
        for (int i = 0; i < ratios.length; i++) {
            shares[i] = amount * (ratios[i] / sum);
        }
        return ResponseEntity.ok(shares);
    }

    @Operation(summary = "Split bill by items")
    @GetMapping("/byItems")
    public ResponseEntity<Map<String, Double>> splitByItems(
            @Parameter(description = "Map of items and their prices") @RequestParam Map<String, Double> items,
            @Parameter(description = "List of participants") @RequestParam List<String> participants) {
        log.debug("Splitting bill by items: {} among participants: {}", items, participants);
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("At least one participant is required");
        }
        
        double totalAmount = items.values().stream().mapToDouble(Double::doubleValue).sum();
        double amountPerPerson = totalAmount / participants.size();
        
        Map<String, Double> shares = new HashMap<>();
        participants.forEach(participant -> shares.put(participant, amountPerPerson));
        
        return ResponseEntity.ok(shares);
    }
} 