#!/bin/bash

# MySQL connection parameters
MYSQL_USER="root"
MYSQL_PASSWORD="root"
MYSQL_HOST="localhost"
MYSQL_PORT="3306"

# Create database and table
mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD << EOF
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
EOF

# Check if the database was created successfully
if [ $? -eq 0 ]; then
    echo "Database and table created successfully!"
else
    echo "Error creating database and table"
    exit 1
fi 