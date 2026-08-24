# Banking API 🏦

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Tests](https://img.shields.io/badge/tests-61%20passed-success)
![CI](https://github.com/batuhantptnci/banking-api/actions/workflows/ci.yml/badge.svg)

A portfolio-grade banking backend application built with **Java 21, Spring Boot, PostgreSQL, Spring Security, JWT, Flyway and Docker**.

The project demonstrates real-world backend concepts including authentication, role-based authorization, account ownership, money transfers, transaction history, database migrations, concurrency protection, secure refresh token rotation and automated CI testing.

---

## 🚀 Features

### Authentication & Security

- User registration
- User login
- BCrypt password hashing
- JWT access token authentication
- Refresh token authentication
- Refresh token rotation
- Expired refresh token validation
- Invalid refresh token handling
- Refresh token reuse prevention
- SHA-256 hashed refresh token storage
- Concurrent refresh token protection
- Stateless Spring Security configuration
- Role-Based Authorization
- `USER` and `ADMIN` roles
- Admin-only user management endpoints
- `401 Unauthorized` and `403 Forbidden` security handling

### User Management

- Create users
- Get all users
- Get user by ID
- Update users
- Delete users
- Email uniqueness validation
- Request validation
- Role-based access protection

### Account Management

- Create bank accounts
- Automatically generated account numbers
- View authenticated user's accounts
- View account details
- Account ownership validation
- Deposit money
- Withdraw money
- Balance validation
- Pessimistic database locking

### Transactions

- Deposit
- Withdraw
- Transfer between accounts
- Transaction history
- Sender ownership validation
- Insufficient balance protection
- Concurrent transaction protection
- Deterministic account locking during transfers

### Database

- PostgreSQL
- Spring Data JPA
- Hibernate
- Flyway migrations
- Database constraints
- Foreign keys
- Transaction indexes
- Schema validation

### Testing & CI

- Unit tests
- Controller tests
- Service tests
- Integration tests
- Security tests
- Role authorization tests
- Account concurrency tests
- Refresh token tests
- Refresh token rotation integration test
- Refresh token concurrency test
- Refresh token hash storage test
- GitHub Actions CI
- PostgreSQL service inside CI

**Current test suite: 61 tests passing ✅**

---

# 🛠 Tech Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT / JJWT
- PostgreSQL 17
- Flyway
- Maven
- Docker
- Docker Compose
- JUnit
- Mockito
- MockMvc
- Git
- GitHub Actions
- Swagger / OpenAPI

---

# 📁 Project Structure

```text
src/main/java/com/batuhan/bankingapi
│
├── config
│   ├── JwtAuthFilter
│   └── SecurityConfig
│
├── controller
│   ├── AuthController
│   ├── UserController
│   └── AccountController
│
├── dto
│   ├── AuthResponse
│   ├── CreateUserRequest
│   ├── LoginRequest
│   ├── RefreshTokenRequest
│   ├── UpdateUserRequest
│   ├── UserResponse
│   └── ...
│
├── entity
│   ├── User
│   ├── Account
│   ├── Transaction
│   ├── RefreshToken
│   ├── Role
│   └── TransactionType
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── InvalidCredentialsException
│   ├── InvalidRefreshTokenException
│   └── ...
│
├── mapper
│
├── repository
│   ├── UserRepository
│   ├── AccountRepository
│   ├── TransactionRepository
│   └── RefreshTokenRepository
│
└── service
    ├── AuthService
    ├── JwtService
    ├── RefreshTokenService
    ├── UserService
    ├── AccountService
    └── TransactionService
```

---

# 🔐 Authentication Flow

## Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "12345678"
}
```

Example response:

```json
{
  "token": "ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```

Newly registered users receive the default role:

```text
USER
```

Users cannot assign themselves the `ADMIN` role during registration.

---

## Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "john@example.com",
  "password": "12345678"
}
```

Example response:

```json
{
  "token": "ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```

The JWT contains the authenticated user's role.

---

# 🔄 Refresh Token Flow

```http
POST /api/auth/refresh
```

Request:

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

Successful response:

```json
{
  "token": "NEW_ACCESS_TOKEN",
  "refreshToken": "NEW_REFRESH_TOKEN",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```

Refresh tokens use **rotation**.

When a refresh token is successfully used:

```text
Old Refresh Token
        ↓
Validate
        ↓
Delete Old Token
        ↓
Generate New Access Token
        ↓
Generate New Refresh Token
```

The old refresh token becomes invalid immediately.

Trying to reuse it returns:

```http
401 Unauthorized
```

Example response:

```json
{
  "message": "Geçersiz refresh token"
}
```

---

# 🔒 Secure Refresh Token Storage

Raw refresh tokens are **never stored directly in the database**.

The client receives the original random token:

```text
Random Refresh Token
```

Before storage, the backend calculates:

```text
SHA-256(refreshToken)
```

Only the resulting **64-character hash** is stored in PostgreSQL.

```text
Client
  ↓
Raw Refresh Token
  ↓
SHA-256
  ↓
Database
  ↓
token_hash
```

This provides additional protection if the database is compromised.

---

# 🔒 Refresh Token Concurrency Protection

Refresh tokens are protected using a PostgreSQL pessimistic write lock.

The repository locks the refresh token row while it is being used.

```text
Request A ─┐
           ├── Same Refresh Token
Request B ─┘
               ↓
        PESSIMISTIC_WRITE 🔒
               ↓
        Request A succeeds
               ↓
        Old token deleted
               ↓
        Request B rejected
```

This prevents two simultaneous requests from successfully using the same refresh token.

---

# 🛡 Role-Based Authorization

The application currently supports:

```text
USER
ADMIN
```

JWT tokens contain a role claim.

Spring Security converts this role into an authority:

```text
USER  → ROLE_USER
ADMIN → ROLE_ADMIN
```

User management endpoints are restricted to administrators.

```text
/api/auth/**      → PUBLIC
/swagger-ui/**    → PUBLIC
/v3/api-docs/**   → PUBLIC

/api/users/**     → ROLE_ADMIN

all other API
endpoints         → AUTHENTICATED
```

A normal authenticated user attempting to access:

```http
GET /api/users
```

receives:

```http
403 Forbidden
```

An administrator receives:

```http
200 OK
```

---

# 👤 User Endpoints

User management endpoints require:

```text
ROLE_ADMIN
```

### Get All Users

```http
GET /api/users
```

### Get User

```http
GET /api/users/{id}
```

### Create User

```http
POST /api/users
```

### Update User

```http
PUT /api/users/{id}
```

or

```http
PATCH /api/users/{id}
```

### Delete User

```http
DELETE /api/users/{id}
```

---

# 💳 Account Endpoints

Account endpoints require authentication.

### Create Account

```http
POST /api/accounts
```

Accounts are automatically associated with the authenticated user.

### My Accounts

```http
GET /api/accounts/me
```

### Get Account

```http
GET /api/accounts/{id}
```

Users can only access accounts they own.

---

# 💰 Deposit

```http
POST /api/accounts/{id}/deposit
```

The account is loaded using a pessimistic database lock.

This protects the balance from concurrent updates.

---

# 💸 Withdraw

```http
POST /api/accounts/{id}/withdraw
```

The API verifies:

```text
Authenticated user
        ↓
Account ownership
        ↓
Pessimistic lock
        ↓
Balance check
        ↓
Withdraw
```

If the balance is insufficient:

```http
400 Bad Request
```

---

# 🔁 Transfer

Money can be transferred between accounts.

The transfer implementation:

1. Determines account lock order
2. Locks both accounts
3. Validates sender ownership
4. Validates sender balance
5. Updates both balances
6. Creates the transaction record

Accounts are locked in deterministic ID order to reduce deadlock risk.

---

# 📜 Transaction History

Authenticated users can access transaction history only for accounts they own.

Transactions currently support:

```text
DEPOSIT
WITHDRAW
TRANSFER
```

Transaction history is ordered by creation time.

---

# ⚡ Concurrency Protection

Bank balances must remain correct even when multiple requests arrive at the same time.

The project uses:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

for critical balance operations.

A concurrency integration test verifies this scenario:

```text
Starting balance: 1000

Thread A → Withdraw 800
Thread B → Withdraw 800
```

Expected result:

```text
One withdrawal succeeds ✅
One withdrawal fails ✅
Final balance = 200 ✅
```

The same concurrency principle is also applied to refresh token rotation.

---

# 🗄 Database Migrations

Database schema changes are managed using **Flyway**.

Migration files are located in:

```text
src/main/resources/db/migration
```

Current migrations:

```text
V1__initial_schema.sql
V2__add_transaction_created_at_index.sql
V3__add_role_to_users.sql
V4__create_refresh_tokens_table.sql
V5__hash_refresh_tokens.sql
```

### V1

Creates the initial tables:

```text
users
accounts
transactions
```

### V2

Adds an index for:

```text
transactions.created_at
```

### V3

Adds:

```text
users.role
```

Existing users default to:

```text
USER
```

### V4

Creates:

```text
refresh_tokens
```

with:

```text
id
token
expires_at
user_id
```

### V5

Migrates refresh token storage from raw tokens to SHA-256 hashes.

```text
token
   ↓
token_hash
```

The database now stores only the hash of each refresh token.

---

# 🧪 Testing

The project currently contains:

```text
61 automated tests
```

covering areas such as:

- User service
- User controller
- Authentication
- JWT security
- Account operations
- Deposits
- Withdrawals
- Transfers
- Transaction history
- Account ownership
- Role authorization
- Database migrations
- Concurrent withdrawals
- Refresh token validation
- Refresh token rotation
- Refresh token reuse rejection
- Concurrent refresh requests
- Refresh token SHA-256 storage

Important integration tests include:

```text
AuthIntegrationTest

AccountIntegrationTest

AccountConcurrencyIntegrationTest

RoleAuthorizationIntegrationTest

RefreshTokenIntegrationTest

RefreshTokenConcurrencyIntegrationTest

RefreshTokenHashIntegrationTest
```

Run all tests:

```powershell
.\mvnw.cmd "-Dspring.datasource.password=$env:DB_PASSWORD" "-Djwt.secret=$env:JWT_SECRET" test
```

Current result:

```text
Tests run: 61
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

# 🔄 CI/CD

The project uses **GitHub Actions**.

Workflow:

```text
.github/workflows/ci.yml
```

On CI:

```text
GitHub Actions
      ↓
Java 21
      ↓
PostgreSQL 17
      ↓
Flyway migrations
      ↓
Spring Boot context
      ↓
61 automated tests
      ↓
BUILD SUCCESS ✅
```

Secrets such as database passwords and JWT secrets are not committed to the repository.

---

# ⚙️ Environment Variables

The application expects:

```text
DB_PASSWORD
JWT_SECRET
```

Example `application.properties` configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=banking_user
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

Sensitive values should always be supplied through environment variables or secret management systems.

---

# 🐘 PostgreSQL

Default local configuration:

```text
Database: banking_db
User: banking_user
Port: 5432
```

The database can be inspected using tools such as **DBeaver**.

---

# 📖 Swagger

When the application is running, API documentation is available through Swagger UI.

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

---

# 🚦 HTTP Status Codes

The API uses meaningful HTTP response codes.

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

Examples:

```text
Invalid credentials
→ 401 Unauthorized

Invalid / expired refresh token
→ 401 Unauthorized

User accessing admin endpoint
→ 403 Forbidden

Account does not exist
→ 404 Not Found

Account belongs to another user
→ 403 Forbidden

Insufficient balance
→ 400 Bad Request

Duplicate email
→ 409 Conflict
```

---

# 🗺 Roadmap

Completed:

- [x] User CRUD
- [x] Validation
- [x] Global exception handling
- [x] PostgreSQL
- [x] Account management
- [x] Deposit
- [x] Withdraw
- [x] Transfer
- [x] Transaction history
- [x] Account ownership
- [x] JWT authentication
- [x] BCrypt password hashing
- [x] Spring Security
- [x] Role-Based Authorization
- [x] Flyway migrations
- [x] Database indexing
- [x] Account concurrency protection
- [x] Refresh Tokens
- [x] Refresh Token Rotation
- [x] Refresh Token Concurrency Protection
- [x] Hashed Refresh Token Storage
- [x] Unit Tests
- [x] Integration Tests
- [x] GitHub Actions CI

Possible next improvements:

- [ ] Logout / Refresh Token Revocation
- [ ] Access token expiration strategy
- [ ] Admin role management
- [ ] Pagination
- [ ] Account status management
- [ ] Transaction limits
- [ ] Audit logging
- [ ] Dockerized application
- [ ] Production configuration
- [ ] API versioning
- [ ] Rate limiting

---

# 📌 Current Status

The core banking backend currently supports:

```text
Authentication ✅
Authorization ✅
User Management ✅
Accounts ✅
Deposits ✅
Withdrawals ✅
Transfers ✅
Transaction History ✅
Concurrency Protection ✅
Flyway Migrations ✅
Refresh Token Rotation ✅
Hashed Refresh Token Storage ✅
61 Automated Tests ✅
GitHub Actions CI ✅
```

The next development focus is **logout / refresh token revocation and authentication hardening**.

---

# 👨‍💻 Author

**Batuhan**

GitHub:

```text
https://github.com/batuhantptnci
```

Repository:

```text
https://github.com/batuhantptnci/banking-api
```