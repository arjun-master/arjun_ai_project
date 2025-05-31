# Math Operations API

A Spring Boot REST API that provides basic mathematical operations.

## Features

- Basic arithmetic operations (addition, subtraction, multiplication, division)
- Swagger UI documentation
- Docker support
- Error handling

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional)

## Building the Application

```bash
mvn clean package
```

## Running the Application

### Using Java

```bash
java -jar target/arjun-ai-project-1.0.0.jar
```

### Using Docker

```bash
# Build the Docker image
docker build -t math-operations-api .

# Run the container
docker run -p 8080:8080 math-operations-api
```

## API Documentation

Once the application is running, you can access the Swagger UI documentation at:
http://localhost:8080/swagger-ui.html

## API Endpoints

- GET /api/math/add - Add two numbers
- GET /api/math/subtract - Subtract two numbers
- GET /api/math/multiply - Multiply two numbers
- GET /api/math/divide - Divide two numbers

## Example Usage

```bash
# Addition
curl "http://localhost:8080/api/math/add?a=5&b=3"

# Subtraction
curl "http://localhost:8080/api/math/subtract?a=10&b=4"

# Multiplication
curl "http://localhost:8080/api/math/multiply?a=6&b=7"

# Division
curl "http://localhost:8080/api/math/divide?a=15&b=3"
```
