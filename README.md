# Arjun AI Project

A Spring Boot REST API project that provides mathematical operations through RESTful endpoints. This project demonstrates the use of modern Spring Boot features, Swagger documentation, and Docker containerization.

## Technology Stack

- Java 17
- Spring Boot 3.2.3
- SpringDoc OpenAPI (Swagger) 2.3.0
- Maven
- Docker
- Lombok

## Features

- RESTful API endpoints for mathematical operations
- Comprehensive API documentation using Swagger/OpenAPI
- Docker containerization
- Error handling and validation
- Logging using SLF4J with Lombok
- Clean code architecture

## Project Structure

```
src/main/java/com/arjunai/project/
├── ArjunAiProjectApplication.java
├── config/
│   └── SwaggerConfig.java
└── controllers/
    └── MathOperationsController.java
```

## Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- Docker (optional)

### Building the Application

```bash
# Clone the repository
git clone <repository-url>
cd arjun-ai-project

# Build with Maven
mvn clean package
```

### Running the Application

#### Using Java
```bash
java -jar target/arjun-ai-project-1.0.0.jar
```

#### Using Docker
```bash
# Build Docker image
docker build -t arjun-ai-project .

# Run container
docker run -p 8080:8080 arjun-ai-project
```

## API Documentation

### Math Operations API

#### Endpoints

| Method | URL | Description | Parameters |
|--------|-----|-------------|------------|
| GET | `/api/math/add` | Add two numbers | `a`, `b` (double) |
| GET | `/api/math/subtract` | Subtract two numbers | `a`, `b` (double) |
| GET | `/api/math/multiply` | Multiply two numbers | `a`, `b` (double) |
| GET | `/api/math/divide` | Divide two numbers | `a`, `b` (double) |

#### Example Requests

```bash
# Addition
curl "http://localhost:8080/api/math/add?a=5&b=3"
# Response: 8.0

# Subtraction
curl "http://localhost:8080/api/math/subtract?a=10&b=4"
# Response: 6.0

# Multiplication
curl "http://localhost:8080/api/math/multiply?a=6&b=7"
# Response: 42.0

# Division
curl "http://localhost:8080/api/math/divide?a=15&b=3"
# Response: 5.0
```

### Payment Split API

#### Endpoints

| Method | URL | Description | Parameters |
|--------|-----|-------------|------------|
| GET | `/api/split/equal` | Split amount equally | `amount`, `people` |
| GET | `/api/split/with-tip` | Split with tip | `amount`, `people`, `tipPercentage` |
| POST | `/api/split/custom` | Split by custom ratios | `amount`, `ratios` |
| GET | `/api/split/by-items` | Split by items | `items`, `participants` |

#### Example Requests

```bash
# Equal split
curl "http://localhost:8080/api/split/equal?amount=100&people=4"
# Response: 25.0

# Split with tip
curl "http://localhost:8080/api/split/with-tip?amount=100&people=4&tipPercentage=15"
# Response: 28.75
```

## API Documentation UI

The API documentation is available through Swagger UI when the application is running:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Specification: http://localhost:8080/v3/api-docs

## Error Handling

The API includes comprehensive error handling for:
- Invalid input parameters
- Division by zero
- Invalid number of participants
- Server errors

## Configuration

### Application Properties
```properties
# Server Configuration
server.port=8080

# Logging Configuration
logging.level.com.arjunai.project=DEBUG

# Swagger Configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

Arjun Raju
- GitHub: [@arjunraju](https://github.com/arjunraju)

## Support

For support, please open an issue in the GitHub repository or contact the development team.
