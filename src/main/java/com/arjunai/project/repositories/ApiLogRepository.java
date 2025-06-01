package com.arjunai.project.repositories;

import com.arjunai.project.models.ApiLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApiLogRepository extends JpaRepository<ApiLog, Long> {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
            INSERT INTO api_logs (
                method_name, endpoint, request, response, 
                request_time, response_time, execution_time_ms, 
                error_message, successful
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT * FROM api_logs WHERE id = ?
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT * FROM api_logs ORDER BY request_time DESC
            """;

    private final RowMapper<ApiLog> rowMapper = (rs, rowNum) -> ApiLog.builder()
            .id(rs.getLong("id"))
            .methodName(rs.getString("method_name"))
            .endpoint(rs.getString("endpoint"))
            .request(rs.getString("request"))
            .response(rs.getString("response"))
            .requestTime(rs.getTimestamp("request_time").toInstant())
            .responseTime(rs.getTimestamp("response_time").toInstant())
            .executionTimeMs(rs.getLong("execution_time_ms"))
            .errorMessage(rs.getString("error_message"))
            .successful(rs.getBoolean("successful"))
            .build();

    public ApiLog save(ApiLog apiLog) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, apiLog.getMethodName());
            ps.setString(2, apiLog.getEndpoint());
            ps.setString(3, apiLog.getRequest());
            ps.setString(4, apiLog.getResponse());
            ps.setTimestamp(5, Timestamp.from(apiLog.getRequestTime()));
            ps.setTimestamp(6, Timestamp.from(apiLog.getResponseTime()));
            ps.setLong(7, apiLog.getExecutionTimeMs());
            ps.setString(8, apiLog.getErrorMessage());
            ps.setBoolean(9, apiLog.isSuccessful());
            return ps;
        }, keyHolder);

        apiLog.setId(keyHolder.getKey().longValue());
        return apiLog;
    }

    public ApiLog findById(Long id) {
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, rowMapper, id);
    }

    public List<ApiLog> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, rowMapper);
    }

    @Query("SELECT AVG(a.executionTimeMs) FROM ApiLog a WHERE a.methodName = :methodName AND a.successful = true")
    double findAverageExecutionTimeByMethodName(@Param("methodName") String methodName) {
        // Implementation of the method
        return 0.0; // Placeholder return, actual implementation needed
    }
} 