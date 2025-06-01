package com.arjunai.project.repositories;

import com.arjunai.project.models.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
    @Query("SELECT AVG(a.executionTimeMs) FROM ApiLog a WHERE a.methodName = :methodName AND a.successful = true")
    double findAverageExecutionTimeByMethodName(@Param("methodName") String methodName);
} 