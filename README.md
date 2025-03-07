# Flight Management API

A secure REST API for managing flight information with JWT-based authentication and role-based access control.

## Features

- JWT Authentication with role-based security
- Flight management (add, retrieve, delete)
- Role-based access control (Admin vs User)
- Comprehensive input validation
- Exception handling
- Robust unit and controller tests
- API documentation with Swagger/OpenAPI

## Technologies

- Spring Boot 3.2.3
- Spring Security with JWT
- Spring Data JPA
- Oracle Database (production) / H2 Database (tests)
- JWT (JSON Web Tokens)
- Swagger/OpenAPI 3.0
- Maven
- JUnit 5 & Mockito for testing

## Project Structure

The application follows a standard layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Manages data access
- **Security Layer**: Handles authentication and authorization
- **Model Layer**: Entity definitions
- **DTO Layer**: Data transfer objects
- **Exception Handling**: Custom exceptions and global handler
- **Test Layer**: Unit tests, controller tests, and security tests

## API Endpoints

### Authentication

- `POST /auth/login` - Authenticate and get JWT token

### Flight Management

- `POST /flights` - Add a new flight (Admin only)
- `GET /flights/{id}` - Get a flight by ID (Authenticated users)
- `GET /flights` - List all flights (Authenticated users)
- `DELETE /flights/{id}` - Delete a flight (Admin only)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Oracle Database (can be replaced with H2 for development)

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The API will be available at http://localhost:8080/flightapp
The Swagger UI will be available at http://localhost:8080/flightapp/swagger-ui.html

### Using the API

You can interact with the API in two ways:

1. **Using Swagger UI**
   
   Navigate to http://localhost:8080/flightapp/swagger-ui.html to access the Swagger UI interface.
   - This provides interactive documentation for all endpoints
   - You can test all API endpoints directly from the browser
   - To use authenticated endpoints, first execute the login endpoint to get a token, then click the "Authorize" button and enter `Bearer YOUR_JWT_TOKEN`

2. **Using cURL or other HTTP clients**

   Use the following curl command to authenticate:

   ```bash
   curl -X POST http://localhost:8080/flightapp/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123"}'
   ```

   Response:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "username": "admin",
     "role": "ADMIN"
   }
   ```

3. **Creating a Flight (Admin only)**

   ```bash
   curl -X POST http://localhost:8080/flightapp/flights \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{
       "carrierCode": "AA",
       "flightNumber": "1234",
       "flightDate": "2025-03-15",
       "origin": "JFK",
       "destination": "LAX"
     }'
   ```

4. **Getting a Flight**

   ```bash
   curl -X GET http://localhost:8080/flightapp/flights/1 \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

5. **Getting All Flights**

   ```bash
   curl -X GET http://localhost:8080/flightapp/flights \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

6. **Deleting a Flight (Admin only)**

   ```bash
   curl -X DELETE http://localhost:8080/flightapp/flights/1 \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

## Default Users

The application comes with two pre-configured users:

1. **Admin User**
   - Username: `admin`
   - Password: `123`
   - Role: `ADMIN`

2. **Regular User**
   - Username: `user`
   - Password: `123`
   - Role: `USER`

## Database 

### Oracle Database (Production)
The application uses Oracle as its primary database:

- Database: Oracle XE
- Username: C##AIRLINEFLIGHT
- Password: airline123

### H2 Database (Testing)
For testing purposes, the application switches to an in-memory H2 database:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `` (empty)

## Testing

The application includes comprehensive tests covering different components:

### Test Structure

- **Controller Tests**: Tests that validate controller endpoints using MockMvc and mocked services
- **Service Unit Tests**: Tests for service layer business logic
- **Security Configuration Tests**: Tests that validate security rules and permissions
- **Component Unit Tests**: Tests for individual components like JwtTokenProvider

### Testing Approach

Our testing approach focuses on:

- Testing controller endpoints with Spring's `@WebMvcTest`
- Mocking services with Mockito to isolate controller behavior
- Using `@WithMockUser` to simulate authenticated users with specific roles
- Validating role-based access control at controller level
- Testing security configuration with `@SpringBootTest`
- Testing JWT token generation and validation

### Security Testing Features

- TestSecurityConfig: Configures security for the test environment
- TestMethodSecurityConfig: Ensures method-level security annotations work in tests
- WithMockUser annotations: Simulates authenticated users with specific roles
- CSRF token handling in tests
- Proper validation of authorization failures (403 Forbidden responses)

### Running the Tests

To run the tests:

```bash
mvn test
```

## Security Considerations

- JWT tokens expire after 1 hour (configurable in application.properties)
- Passwords are stored as BCrypt hashes
- API endpoints are secured with role-based authorization
- Method-level security with @PreAuthorize annotations for fine-grained access control
- CSRF protection enabled for production (disabled for testing)
- REST principles are followed for API design
- Comprehensive exception handling for security violations