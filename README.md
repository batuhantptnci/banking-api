# Banking API

A RESTful banking backend application built with Java, Spring Boot, PostgreSQL and Docker.

The project is being developed as a portfolio project with the goal of building a complete banking system including user management, bank accounts, transactions, authentication and a mobile client.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- Maven
- Lombok
- Jakarta Validation
- Git & GitHub

## Current Features

### User Management

- Create user
- Get all users
- Get user by ID
- Update user
- Delete user
- Email uniqueness validation
- Request validation
- Global exception handling

### HTTP Status Handling

- `201 Created` - User created successfully
- `204 No Content` - User deleted successfully
- `400 Bad Request` - Invalid request data
- `404 Not Found` - User not found
- `409 Conflict` - Email already exists

## Project Structure

```text
controller
    Handles HTTP requests

dto
    Defines API request and response models

entity
    Represents database entities

exception
    Handles application errors

mapper
    Converts entities and DTOs

repository
    Communicates with the database

service
    Contains business logic
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

## Environment Variables

The application uses environment variables for sensitive configuration.

```text
DB_PASSWORD=your_postgresql_password
```

## Roadmap

- [x] User CRUD
- [x] DTO structure
- [x] Validation
- [x] Global exception handling
- [x] PostgreSQL integration
- [x] Dockerized PostgreSQL
- [ ] Bank accounts
- [ ] Account balances
- [ ] Deposit and withdrawal
- [ ] Money transfers
- [ ] Transaction history
- [ ] Authentication
- [ ] JWT authorization
- [ ] Unit and integration tests
- [ ] Docker Compose
- [ ] CI/CD with GitHub Actions
- [ ] Mobile banking application

## Architecture

The application currently follows a layered architecture:

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

DTOs are used at the API boundary, while entities remain inside the application.

## Status

🚧 This project is actively under development.