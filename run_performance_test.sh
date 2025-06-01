#!/bin/bash

# Start Spring Boot application in the background
echo "Starting Spring Boot application..."
mvn spring-boot:run &
SPRING_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 30

# Run JMeter test
echo "Running JMeter performance test..."
jmeter -n -t src/test/resources/performance_test_plan.jmx \
       -l performance_test_results.jtl \
       -e -o performance_test_report

# Stop Spring Boot application
echo "Stopping Spring Boot application..."
kill $SPRING_PID

# Analyze database performance
echo "Analyzing database performance..."
mysql -u root -p my_db << EOF
SELECT 
    method_name,
    COUNT(*) as total_calls,
    AVG(execution_time_ms) as avg_execution_time,
    MAX(execution_time_ms) as max_execution_time,
    MIN(execution_time_ms) as min_execution_time,
    SUM(CASE WHEN successful = 1 THEN 1 ELSE 0 END) as successful_calls,
    SUM(CASE WHEN successful = 0 THEN 1 ELSE 0 END) as failed_calls
FROM api_logs 
WHERE request_time >= NOW() - INTERVAL 15 MINUTE
GROUP BY method_name
ORDER BY avg_execution_time DESC;

SELECT 
    MINUTE(request_time) as minute,
    COUNT(*) as requests_per_minute,
    AVG(execution_time_ms) as avg_execution_time
FROM api_logs 
WHERE request_time >= NOW() - INTERVAL 15 MINUTE
GROUP BY MINUTE(request_time)
ORDER BY minute;
EOF

echo "Performance test completed. Check performance_test_report directory for detailed results." 