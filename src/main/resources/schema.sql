CREATE DATABASE IF NOT EXISTS my_db;
USE my_db;

CREATE TABLE IF NOT EXISTS api_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    method_name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request LONGTEXT,
    response LONGTEXT,
    request_time TIMESTAMP NOT NULL,
    response_time TIMESTAMP NOT NULL,
    execution_time_ms BIGINT NOT NULL,
    error_message LONGTEXT,
    successful BOOLEAN NOT NULL,
    INDEX idx_method_name (method_name),
    INDEX idx_request_time (request_time),
    INDEX idx_successful (successful)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 