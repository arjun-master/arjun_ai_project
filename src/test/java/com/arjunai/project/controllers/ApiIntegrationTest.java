package com.arjunai.project.controllers;

import com.arjunai.project.models.ApiLog;
import com.arjunai.project.repositories.ApiLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.converter.HttpMessageNotReadableException;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long getLastInsertedId() {
        return jdbcTemplate.queryForObject(
            "SELECT MAX(id) FROM api_logs", Long.class);
    }

    @Test
    @Order(1)
    public void testMathOperations() throws Exception {
        // Get initial count and max ID
        Long initialCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs", Long.class);
        Long initialMaxId = getLastInsertedId();

        // Test add operation
        mockMvc.perform(get("/api/math/add")
                .param("a", "5")
                .param("b", "3"))
                .andExpect(status().isOk());

        // Test subtract operation
        mockMvc.perform(get("/api/math/subtract")
                .param("a", "10")
                .param("b", "4"))
                .andExpect(status().isOk());

        // Test multiply operation
        mockMvc.perform(get("/api/math/multiply")
                .param("a", "6")
                .param("b", "7"))
                .andExpect(status().isOk());

        // Test divide operation
        mockMvc.perform(get("/api/math/divide")
                .param("a", "15")
                .param("b", "3"))
                .andExpect(status().isOk());

        // Verify database records for math operations
        List<Map<String, Object>> mathLogs = jdbcTemplate.queryForList(
            "SELECT * FROM api_logs WHERE endpoint LIKE '/api/math/%' AND id > ? ORDER BY id",
            initialMaxId != null ? initialMaxId : 0
        );
        assertEquals(4, mathLogs.size(), "Should have 4 new math operation records");
        
        // Verify successful operations and auto-incrementing IDs
        Long lastId = null;
        for (Map<String, Object> log : mathLogs) {
            assertTrue((Boolean) log.get("successful"), "All operations should be successful");
            assertNotNull(log.get("request_time"), "Request time should be recorded");
            assertNotNull(log.get("response_time"), "Response time should be recorded");
            assertTrue((Long) log.get("execution_time_ms") >= 0, "Execution time should be non-negative");
            
            // Verify auto-incrementing IDs
            Long currentId = ((Number) log.get("id")).longValue();
            if (lastId != null) {
                assertTrue(currentId > lastId, "IDs should be auto-incrementing");
            }
            lastId = currentId;
        }

        // Verify total count increased
        Long finalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs", Long.class);
        assertEquals(initialCount + 4, finalCount, "Total count should increase by 4");
    }

    @Test
    @Order(2)
    public void testPaymentSplitOperations() throws Exception {
        // Get initial count and max ID
        Long initialCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = true", Long.class);
        Long initialMaxId = getLastInsertedId();

        // Test equal split
        mockMvc.perform(get("/api/split/equal")
                .param("amount", "100")
                .param("people", "4"))
                .andExpect(status().isOk());

        // Test split with tip
        mockMvc.perform(get("/api/split/with-tip")
                .param("amount", "100")
                .param("people", "4")
                .param("tipPercentage", "15"))
                .andExpect(status().isOk());

        // Test custom split
        mockMvc.perform(post("/api/split/custom")
                .param("amount", "100")
                .param("ratios", "1", "2", "3"))
                .andExpect(status().isOk());

        // Test split by items with proper JSON format
        mockMvc.perform(post("/api/split/byItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"item1\":50.0,\"item2\":30.0}")
                .param("participants", "Alice", "Bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Alice").value(40.0))
                .andExpect(jsonPath("$.Bob").value(40.0));

        // Test invalid items format
        mockMvc.perform(post("/api/split/byItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .param("participants", "Alice", "Bob"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));

        // Verify database records for split operations
        List<Map<String, Object>> splitLogs = jdbcTemplate.queryForList(
            "SELECT * FROM api_logs WHERE endpoint LIKE '/api/split/%' AND id > ? AND successful = true ORDER BY id",
            initialMaxId != null ? initialMaxId : 0
        );
        assertEquals(4, splitLogs.size(), "Should have 4 new successful split operation records");
        
        // Verify successful operations and auto-incrementing IDs
        Long lastId = null;
        for (Map<String, Object> log : splitLogs) {
            assertTrue((Boolean) log.get("successful"), "All operations should be successful");
            assertNotNull(log.get("request_time"), "Request time should be recorded");
            assertNotNull(log.get("response_time"), "Response time should be recorded");
            assertTrue((Long) log.get("execution_time_ms") >= 0, "Execution time should be non-negative");
            
            // Verify auto-incrementing IDs
            Long currentId = ((Number) log.get("id")).longValue();
            if (lastId != null) {
                assertTrue(currentId > lastId, "IDs should be auto-incrementing");
            }
            lastId = currentId;
        }

        // Verify total count increased
        Long finalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = true", Long.class);
        assertEquals(initialCount + 4, finalCount, "Total count should increase by 4 successful operations");
    }

    @Test
    @Order(3)
    public void testErrorHandling() throws Exception {
        // Get initial count and max ID
        Long initialCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = false", Long.class);
        Long initialMaxId = getLastInsertedId();

        // Test division by zero
        mockMvc.perform(get("/api/math/divide")
                .param("a", "10")
                .param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Cannot divide by zero", result.getResolvedException().getMessage()));

        // Test invalid number of people
        mockMvc.perform(get("/api/split/equal")
                .param("amount", "100")
                .param("people", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));

        // Test invalid items format
        mockMvc.perform(post("/api/split/byItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid_json}")
                .param("participants", "Alice", "Bob"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                    result.getResolvedException() instanceof IllegalArgumentException ||
                    result.getResolvedException() instanceof HttpMessageNotReadableException
                ));

        // Verify error records in database
        List<Map<String, Object>> errorLogs = jdbcTemplate.queryForList(
            "SELECT * FROM api_logs WHERE successful = false AND id > ? ORDER BY id",
            initialMaxId != null ? initialMaxId : 0
        );
        assertEquals(3, errorLogs.size(), "Should have 3 new error records");
        
        // Verify error details and auto-incrementing IDs
        Long lastId = null;
        for (Map<String, Object> log : errorLogs) {
            assertFalse((Boolean) log.get("successful"), "Operations should be marked as failed");
            assertNotNull(log.get("error_message"), "Error message should be recorded");
            
            // Verify auto-incrementing IDs
            Long currentId = ((Number) log.get("id")).longValue();
            if (lastId != null) {
                assertTrue(currentId > lastId, "IDs should be auto-incrementing");
            }
            lastId = currentId;
        }

        // Verify total count increased
        Long finalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = false", Long.class);
        assertEquals(initialCount + 3, finalCount, "Total count should increase by 3 failed operations");
    }

    @Test
    @Order(4)
    public void testDataPersistence() throws Exception {
        // Get total count of records
        Long totalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs", Long.class);
        
        // We should have records from all previous tests
        // 4 math operations + 4 split operations + 3 error cases = 11 records minimum
        assertTrue(totalCount >= 11, "Should have accumulated records from previous tests");

        // Verify we have both successful and failed operations
        Long successCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = true", Long.class);
        Long failureCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM api_logs WHERE successful = false", Long.class);
        
        assertTrue(successCount >= 8, "Should have at least 8 successful operations");
        assertTrue(failureCount >= 3, "Should have at least 3 failed operations");

        // Verify auto-increment is working
        Long maxId = getLastInsertedId();
        assertTrue(maxId >= totalCount, "Max ID should be at least equal to total count");

        // Verify ID sequence
        List<Map<String, Object>> allLogs = jdbcTemplate.queryForList(
            "SELECT id FROM api_logs ORDER BY id"
        );
        Long lastId = null;
        for (Map<String, Object> log : allLogs) {
            Long currentId = ((Number) log.get("id")).longValue();
            if (lastId != null) {
                assertEquals(lastId + 1, currentId, "IDs should be sequential");
            }
            lastId = currentId;
        }
    }
} 