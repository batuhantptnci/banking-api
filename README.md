<div align="center">

# 🏦 Banking API

### Modern, Secure & Tested Banking Backend

A portfolio-grade banking REST API built with **Java 21**, **Spring Boot**, **PostgreSQL**, **JWT Authentication**, **Flyway**, **Docker Compose** and **GitHub Actions**.

<br>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/Auth-JWT-purple?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)
![Tests](https://img.shields.io/badge/Tests-53%20Passing-success?style=for-the-badge)

![CI](https://github.com/batuhantptnci/banking-api/actions/workflows/ci.yml/badge.svg)

</div>

---

## ✨ Features

### 🔐 Authentication & Security

- ✅ User registration
- ✅ User login
- ✅ BCrypt password hashing
- ✅ JWT token generation
- ✅ JWT token validation
- ✅ Stateless authentication
- ✅ Protected API endpoints
- ✅ Account ownership validation
- ✅ Unauthorized access protection
- ✅ Secure Swagger authorization

---

### 👤 Users

- ✅ Create users
- ✅ Get users
- ✅ Update users
- ✅ Delete users
- ✅ Email uniqueness validation
- ✅ Request validation
- ✅ Secure password storage

---

### 💳 Accounts

- ✅ Create bank accounts
- ✅ Automatic account number generation
- ✅ List authenticated user's accounts
- ✅ Get account details
- ✅ Account ownership checks
- ✅ Initial balance: `0.00`
- ✅ Database row locking for balance-changing operations
- ✅ Concurrent balance update protection

Example account number:

```text
ACC-A1602ADE
```

---

### 💸 Banking Operations

#### Deposit

```text
Account
  ↓
Lock Account 🔒
  ↓
+ Money
  ↓
Updated Balance
```

#### Withdraw

```text
Account
  ↓
Lock Account 🔒
  ↓
Balance Check
  ↓
- Money
  ↓
Updated Balance
```

#### Transfer

```text
Sender Account
      ↓
Lock Accounts 🔒
      ↓
   Amount
      ↓
Receiver Account
```

Transfer operations include:

- ✅ Sender ownership validation
- ✅ Balance validation
- ✅ Same-account transfer protection
- ✅ Atomic database transaction
- ✅ Transaction history creation
- ✅ Pessimistic database locking
- ✅ Deterministic account lock ordering
- ✅ Reduced deadlock risk
- ✅ Concurrent balance protection

---

## 🔒 Concurrency Protection

Balance-changing operations use **pessimistic database locking** to prevent concurrent updates from causing inconsistent balances.

Accounts are loaded using:

```text
PESSIMISTIC_WRITE
```

This ensures that only one transaction can modify a locked account row at a time.

Example double-withdraw scenario:

```text
Initial Balance: 1000.00

Thread 1 → Withdraw 800.00 ✅
Thread 2 → Withdraw 800.00 ❌

Final Balance: 200.00
```

Without proper concurrency protection, simultaneous requests could potentially read the same balance before either update is committed.

With pessimistic locking:

```text
Request 1
   ↓
Account Locked 🔒
   ↓
Balance Updated
   ↓
Transaction Committed
   ↓
Lock Released
   ↓
Request 2
   ↓
Reads Latest Balance
```

Transfers lock both accounts in a deterministic order based on account ID.

Example:

```text
Transfer 10 → 5
Lock order: 5 → 10

Transfer 5 → 10
Lock order: 5 → 10
```

Using the same lock order reduces the risk of database deadlocks when opposite transfers happen concurrently.

---

## 📜 Transaction History

Every banking operation creates a transaction record.

Supported transaction types:

```text
DEPOSIT
WITHDRAW
TRANSFER
```

Transaction directions:

```text
INCOMING
OUTGOING
```

Example:

```json
{
  "id": 9,
  "type": "TRANSFER",
  "direction": "OUTGOING",
  "amount": 15000,
  "accountId": 9,
  "targetAccountId": 10,
  "createdAt": "2026-08-19T15:26:37"
}
```

Transactions are returned from newest to oldest.

---

## 🗃️ Database Migrations

Database schema changes are managed with **Flyway**.

Current migrations:

```text
V1__initial_schema.sql
V2__add_transaction_created_at_index.sql
```

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Flyway manages schema changes while Hibernate validates that the database matches the entities.

---

## 🔐 Authentication Flow

```text
┌──────────────┐
│   Register   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ BCrypt Hash  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ JWT Created  │
└──────┬───────┘
       │
       ▼
┌─────────────────────┐
│ Authorization Header│
│ Bearer <JWT_TOKEN>  │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ Protected Endpoints │
└─────────────────────┘
```

---

# 📡 API Overview

## 🔓 Authentication

### Register

```http
POST /api/auth/register
```

Example:

```json
{
  "fullName": "Test User",
  "email": "test@test.com",
  "password": "12345678"
}
```

---

### Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "test@test.com",
  "password": "12345678"
}
```

Response:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": 1,
    "fullName": "Test User",
    "email": "test@test.com"
  }
}
```

---

## 💳 Accounts

### Create Account

```http
POST /api/accounts
```

The authenticated user is automatically detected from the JWT token.

---

### My Accounts

```http
GET /api/accounts/me
```

---

### Get Account

```http
GET /api/accounts/{id}
```

Only the account owner can access the account.

---

### Deposit

```http
POST /api/accounts/{id}/deposit
```

```json
{
  "amount": 1000.00
}
```

---

### Withdraw

```http
POST /api/accounts/{id}/withdraw
```

```json
{
  "amount": 250.00
}
```

---

### Transfer

```http
POST /api/accounts/transfer
```

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 300.00
}
```

---

## 📜 Transactions

### Account Transaction History

```http
GET /api/transactions/account/{accountId}
```

Only the account owner can view the transaction history.

---

# 📚 Swagger / OpenAPI

Interactive API documentation is available with Swagger UI.

Start the application and open:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

### 🔒 Using JWT in Swagger

1. Register or login using `/api/auth/register` or `/api/auth/login`
2. Copy the returned JWT token
3. Click **Authorize 🔒**
4. Paste the JWT token
5. Call protected endpoints directly from Swagger

Swagger automatically sends:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 🐳 Docker Compose

PostgreSQL can be managed using Docker Compose.

Start PostgreSQL:

```bash
docker compose up -d
```

Check container status:

```bash
docker compose ps
```

Stop services:

```bash
docker compose down
```

Database configuration:

```text
Database: banking_db
Username: banking_user
Port:     5432
```

Database passwords are provided through environment variables and are not stored directly in the application configuration.

---

# ⚙️ Environment Variables

The application requires:

```text
DB_PASSWORD
JWT_SECRET
```

Example IntelliJ configuration:

```text
Run
→ Edit Configurations
→ BankingApiApplication
→ Environment Variables
```

Example:

```text
DB_PASSWORD=<your_database_password>
JWT_SECRET=<your_base64_jwt_secret>
```

> ⚠️ Never commit real secrets to Git.

---

# 🧪 Testing

The project contains automated tests for the main business, HTTP, integration and concurrency layers.

```text
AccountServiceTest
AuthServiceTest
UserServiceTest
TransactionServiceTest

AuthControllerTest
AccountControllerTest
TransactionControllerTest

AuthIntegrationTest
AccountIntegrationTest
AccountConcurrencyIntegrationTest

BankingApiApplicationTests
```

Current status:

```text
53 Tests
0 Failures
0 Errors
0 Skipped
✅ BUILD SUCCESS
```

Covered scenarios include:

- ✅ Deposit
- ✅ Withdraw
- ✅ Transfer
- ✅ Insufficient balance
- ✅ Account ownership
- ✅ Invalid transfers
- ✅ Transaction creation
- ✅ Login
- ✅ Invalid credentials
- ✅ Password hashing
- ✅ Duplicate emails
- ✅ User CRUD
- ✅ Request validation
- ✅ HTTP status codes
- ✅ Controller responses
- ✅ Full register & login flow
- ✅ JWT protected endpoint integration
- ✅ Account creation integration
- ✅ Deposit & withdraw integration
- ✅ Transfer integration
- ✅ Transaction history integration
- ✅ Concurrent withdrawal protection
- ✅ Pessimistic database locking
- ✅ Double-withdraw prevention

Run tests:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

When environment variables are required:

```powershell
.\mvnw.cmd "-Dspring.datasource.password=$env:DB_PASSWORD" "-Djwt.secret=$env:JWT_SECRET" test
```

---

# ⚙️ Continuous Integration

GitHub Actions automatically runs the test suite on:

```text
Push → main
Pull Request → main
```

CI pipeline:

```text
Git Push
   ↓
GitHub Actions
   ↓
PostgreSQL 17
   ↓
Flyway Migrations
   ↓
Java 21
   ↓
Maven Tests
   ↓
53 Tests ✅
```

CI credentials are stored securely using **GitHub Repository Secrets**.

---

# 🏗️ Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Additional layers and infrastructure:

```text
DTO
Mapper
Exception Handler
JWT Filter
Security Configuration
OpenAPI Configuration
Flyway Migrations
Pessimistic Database Locking
```

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| ☕ Java 21 | Backend language |
| 🍃 Spring Boot 4.1 | Application framework |
| 🔐 Spring Security | API security |
| 🎫 JWT | Authentication |
| 🗄️ Spring Data JPA | Data access |
| 🔒 JPA Pessimistic Locking | Concurrency protection |
| 🐘 PostgreSQL 17 | Database |
| 🛫 Flyway | Database migrations |
| 🐳 Docker Compose | Database container |
| 📚 Swagger / OpenAPI | API documentation |
| 🧪 JUnit | Testing |
| 🎭 Mockito | Mocking |
| ⚙️ GitHub Actions | CI pipeline |
| 📦 Maven | Dependency management |
| ✨ Lombok | Boilerplate reduction |

---

# 📁 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.batuhan.bankingapi
│   │       ├── config
│   │       │   ├── JwtAuthFilter
│   │       │   ├── OpenApiConfig
│   │       │   └── SecurityConfig
│   │       │
│   │       ├── controller
│   │       │   ├── AccountController
│   │       │   ├── AuthController
│   │       │   ├── TransactionController
│   │       │   └── UserController
│   │       │
│   │       ├── dto
│   │       ├── entity
│   │       ├── exception
│   │       ├── mapper
│   │       ├── repository
│   │       └── service
│   │
│   └── resources
│       └── db
│           └── migration
│               ├── V1__initial_schema.sql
│               └── V2__add_transaction_created_at_index.sql
│
└── test
    └── java
        └── com.batuhan.bankingapi
            ├── controller
            ├── integration
            │   ├── AccountIntegrationTest
            │   ├── AccountConcurrencyIntegrationTest
            │   └── AuthIntegrationTest
            └── service
```

Additional project files:

```text
.github/workflows/ci.yml
compose.yaml
pom.xml
README.md
```

---

# 🛡️ Security Rules

```text
/api/auth/**        → PUBLIC
/swagger-ui/**      → PUBLIC
/v3/api-docs/**     → PUBLIC

All other endpoints → JWT REQUIRED 🔒
```

Accounts are protected by ownership checks.

Example:

```text
User A → User A's Account ✅

User A → User B's Account ❌
                ↓
           403 Forbidden
```

Balance-changing operations are additionally protected against concurrent updates using database row locks.

---

# 🚀 Roadmap

- [x] User CRUD
- [x] Validation
- [x] Global Exception Handling
- [x] Account Management
- [x] Deposit
- [x] Withdraw
- [x] Transfer
- [x] Transaction History
- [x] BCrypt Password Hashing
- [x] Login / Register
- [x] JWT Authentication
- [x] Account Authorization
- [x] Unit Tests
- [x] Controller Tests
- [x] Integration Tests
- [x] Concurrency Integration Test
- [x] Docker Compose
- [x] GitHub Actions CI
- [x] GitHub Secrets
- [x] Swagger / OpenAPI
- [x] Flyway Database Migrations
- [x] Account Locking / Concurrency Protection
- [ ] Refresh Tokens
- [ ] Role Based Authorization
- [ ] Dockerize Spring Boot Application
- [ ] Deployment
- [ ] Flutter Mobile Application

---

# 📱 Future Mobile Application

The API is designed to later power a Flutter banking application.

Planned mobile features:

```text
Login / Register
      ↓
Dashboard
      ↓
My Accounts
      ↓
Deposit / Withdraw
      ↓
Money Transfer
      ↓
Transaction History
```

---

<div align="center">

## 🏦 Project Status

### 🟢 Active Development

**Backend Core Completed ✅**

**Authentication & Security Completed ✅**

**Automated Tests Completed — 53 Passing ✅**

**Integration Tests Completed ✅**

**Concurrency Protection Completed ✅**

**Docker Compose Completed ✅**

**Continuous Integration Completed ✅**

**Swagger Documentation Completed ✅**

**Flyway Database Migrations Completed ✅**

<br>

Built with ☕ Java + 🍃 Spring Boot

</div>