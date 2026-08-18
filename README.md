# Banking API 🏦

A RESTful banking backend application built with Java and Spring Boot.

The project focuses on clean backend architecture, authentication, account ownership, balance operations, transfers, and transaction history.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT Authentication
- BCrypt Password Hashing
- Jakarta Validation
- Maven
- Lombok
- Docker
- Git & GitHub

## Features

### Authentication & Security

- User registration
- User login
- BCrypt password hashing
- JWT token generation
- JWT validation
- Stateless authentication
- Protected API endpoints
- Account ownership authorization
- Unauthorized access handling with `401`
- Forbidden account access handling with `403`

### User Management

- Create user
- Get users
- Get user by ID
- Update user
- Delete user
- Email validation
- Duplicate email protection

### Bank Accounts

- Create an account for the authenticated user
- Automatically generated account numbers
- View authenticated user's accounts
- View a specific owned account
- Account ownership validation
- Balance management

### Banking Operations

- Deposit money
- Withdraw money
- Transfer money between accounts
- Insufficient balance validation
- Same-account transfer prevention
- Secure account ownership checks
- Transaction rollback with `@Transactional`

### Transaction History

- Deposit history
- Withdrawal history
- Transfer history
- Incoming / outgoing transaction direction
- Account-specific transaction history
- Secure transaction history access
- Transactions ordered by newest first

## Authentication Flow

```text
Register / Login
       ↓
Email + Password
       ↓
BCrypt Password Validation
       ↓
JWT Generated
       ↓
Authorization: Bearer <token>
       ↓
JwtAuthFilter
       ↓
Protected Endpoint
```

## API Endpoints

### Authentication

#### Register

```http
POST /api/auth/register
```

Example:

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```

#### Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "john@example.com",
  "password": "Password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```

## Account Endpoints

All account endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Create Account

```http
POST /api/accounts
```

No request body is required.

The account is automatically created for the authenticated user.

### Get My Accounts

```http
GET /api/accounts/me
```

### Get My Account By ID

```http
GET /api/accounts/{accountId}
```

Users can only access accounts they own.

### Deposit

```http
POST /api/accounts/{accountId}/deposit
```

```json
{
  "amount": 500
}
```

### Withdraw

```http
POST /api/accounts/{accountId}/withdraw
```

```json
{
  "amount": 100
}
```

Users cannot deposit into or withdraw from accounts they do not own.

### Transfer

```http
POST /api/accounts/transfer
```

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 250
}
```

The authenticated user must own the sender account.

The receiver account may belong to another user.

## Transaction Endpoints

### Account Transaction History

```http
GET /api/transactions/account/{accountId}
```

Only the owner of the account can view its transaction history.

Example response:

```json
[
  {
    "id": 10,
    "type": "TRANSFER",
    "direction": "OUTGOING",
    "amount": 250.00,
    "accountId": 1,
    "targetAccountId": 2,
    "createdAt": "2026-08-18T14:30:00"
  }
]
```

Transaction direction can be:

```text
INCOMING
OUTGOING
```

## HTTP Status Codes

| Status | Meaning |
|---|---|
| `200 OK` | Request completed successfully |
| `201 Created` | Resource created successfully |
| `204 No Content` | Resource deleted successfully |
| `400 Bad Request` | Validation or business rule error |
| `401 Unauthorized` | Authentication required or credentials invalid |
| `403 Forbidden` | User does not own the requested account |
| `404 Not Found` | Resource not found |
| `409 Conflict` | Email already exists |

## Security

Passwords are never stored as plain text.

Passwords are hashed using BCrypt before being saved to PostgreSQL.

JWT tokens are signed using a secret stored outside the source code.

Required environment variables:

```text
DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_base64_jwt_secret
```

Do not commit real secrets to Git.

## Configuration

Example `application.properties`:

```properties
spring.application.name=banking-api

spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=banking_user
spring.datasource.password=${DB_PASSWORD}

spring.jpa.show-sql=true
spring.jpa.properties.jakarta.persistence.schema-generation.database.action=update

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

JWT expiration is currently configured as:

```text
24 hours
```

## Project Structure

```text
src/main/java/com/batuhan/bankingapi
│
├── config
│   ├── JwtAuthFilter
│   └── SecurityConfig
│
├── controller
│   ├── AuthController
│   ├── AccountController
│   ├── TransactionController
│   └── UserController
│
├── dto
│   ├── AccountResponse
│   ├── AccountTransactionResponse
│   ├── AuthResponse
│   ├── CreateUserRequest
│   ├── DepositRequest
│   ├── LoginRequest
│   ├── TransactionDirection
│   ├── TransferRequest
│   ├── UpdateUserRequest
│   ├── UserResponse
│   └── WithdrawRequest
│
├── entity
│   ├── Account
│   ├── Transaction
│   ├── TransactionType
│   └── User
│
├── exception
│
├── mapper
│
├── repository
│
└── service
    ├── AccountService
    ├── AuthService
    ├── JwtService
    ├── TransactionService
    └── UserService
```

## Architecture

The project follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

DTOs are used to separate API requests and responses from database entities.

Mappers are used to convert between entities and DTOs.

Authentication is handled through:

```text
JWT
↓
JwtAuthFilter
↓
Spring Security
↓
SecurityContext
```

## Roadmap

Planned improvements:

- Unit tests
- Integration tests
- Spring Security test coverage
- Docker Compose
- Database migrations
- Improved exception response model
- Account locking / concurrency protection
- Refresh tokens
- Role-based authorization
- Pagination
- CI/CD with GitHub Actions
- Swagger / OpenAPI documentation
- Flutter mobile banking application

## Future Mobile Application 📱

A Flutter mobile application is planned for this backend.

The mobile application will include:

- Register
- Login
- Secure token storage
- Account overview
- Balance display
- Deposit
- Withdraw
- Money transfer
- Transaction history
- Incoming / outgoing transaction UI
- User profile

## Author

Built as a backend banking portfolio project using Java and Spring Boot.