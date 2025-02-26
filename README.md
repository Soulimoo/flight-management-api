# Flight Management API

A secure REST API for managing flight information with JWT-based authentication and role-based access control.

## Features

- JWT Authentication with role-based security
- Flight management (add, retrieve, delete)
- Role-based access control (Admin vs User)
- Comprehensive input validation
- Exception handling
- Unit tests
- API documentation with Swagger/OpenAPI

## Technologies

- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- Swagger/OpenAPI 3.0
- Maven

## Project Structure

The application follows a standard layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Manages data access
- **Security Layer**: Handles authentication and authorization
- **Model Layer**: Entity definitions
- **DTO Layer**: Data transfer objects
- **Exception Handling**: Custom exceptions and global handler

## API Endpoints

### Authentication

- `POST /api/auth/login` - Authenticate and get JWT token

### Flight Management

- `POST /api/flights` - Add a new flight (Admin only)
- `GET /api/flights/{id}` - Get a flight by ID (Authenticated users)
- `GET /api/flights` - List all flights (Authenticated users)
- `DELETE /api/flights/{id}` - Delete a flight (Admin only)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The API will be available at http://localhost:8080/api
The Swagger UI will be available at http://localhost:8080/api/swagger-ui.html

### Using the API

You can interact with the API in two ways:

1. **Using Swagger UI**
   
   Navigate to http://localhost:8080/api/swagger-ui.html to access the Swagger UI interface.
   - This provides interactive documentation for all endpoints
   - You can test all API endpoints directly from the browser
   - To use authenticated endpoints, first execute the login endpoint to get a token, then click the "Authorize" button and enter `Bearer YOUR_JWT_TOKEN`

2. **Using cURL or other HTTP clients**

   Use the following curl command to authenticate:

   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password"}'
   ```

   Response:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "username": "admin",
     "role": "ADMIN"
   }
   ```

2. **Creating a Flight (Admin only)**

   ```bash
   curl -X POST http://localhost:8080/api/flights \
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

3. **Getting a Flight**

   ```bash
   curl -X GET http://localhost:8080/api/flights/1 \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

4. **Getting All Flights**

   ```bash
   curl -X GET http://localhost:8080/api/flights \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

5. **Deleting a Flight (Admin only)**

   ```bash
   curl -X DELETE http://localhost:8080/api/flights/1 \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

## Default Users

The application comes with two pre-configured users:

1. **Admin User**
   - Username: `admin`
   - Password: `password`
   - Role: `ADMIN`

2. **Regular User**
   - Username: `user`
   - Password: `password`
   - Role: `USER`

## Database Console

You can access the H2 database console at http://localhost:8080/api/h2-console with the following details:

- JDBC URL: `jdbc:h2:mem:flightdb`
- Username: `sa`
- Password: `password`

## Testing

To run the tests:

```bash
mvn test
```

## Security Considerations

- JWT tokens expire after 1 hour (configurable in application.properties)
- Passwords are stored as BCrypt hashes
- API endpoints are secured with role-based authorization
- REST principles are followed for API design

## Extending the Application

You can extend this application by:

1. Adding user registration functionality
2. Implementing flight search by various criteria
3. Adding more detailed flight information (aircraft, crew, etc.)
4. Implementing booking functionality
5. Adding pagination and sorting for flight listings
