# Banking API 🏦

A portfolio-grade banking REST API built with **Java 21**, **Spring Boot**, **PostgreSQL**, **Spring Security**, **JWT**, **Flyway** and **Docker**.

The project focuses on real-world backend concepts such as secure authentication, account ownership, money transfers, transaction history, database migrations, concurrency protection and automated CI testing.

---

## 🚀 Features

### 🔐 Authentication & Security
- Customer registration
- Login with **8-digit customer number or 11-digit Turkish National ID**
- Automatically generated unique customer number
- BCrypt password hashing
- JWT access token authentication
- Refresh token rotation
- SHA-256 hashed refresh token storage
- Refresh token reuse protection
- Stateless Spring Security
- Role-based authorization with `USER` and `ADMIN`
- Admin-only user management endpoints

### 👤 Customer Management
- Full name, National ID, phone and email validation
- Unique email, National ID, phone and customer number
- Automatic `USER` role assignment during registration
- Admin-protected user CRUD operations

### 💳 Account Management
- A default account is created automatically after customer registration
- Automatically generated account numbers
- New accounts start with `0.00` balance
- View authenticated customer's accounts
- Account ownership validation
- Deposit and withdraw operations
- Pessimistic locking for critical balance updates

### 💸 Transactions
- Deposit
- Withdraw
- Transfer between accounts
- Transaction history
- Insufficient balance protection
- Sender ownership validation
- Deterministic account locking to reduce deadlock risk
- Concurrent transaction protection

### 🗄 Database & Infrastructure
- PostgreSQL 17
- Spring Data JPA / Hibernate
- Flyway database migrations
- Schema validation
- Database constraints and indexes
- Docker Compose
- Swagger / OpenAPI

### 🧪 Testing & CI
- Unit tests
- Service and controller tests
- Integration tests
- Security and authorization tests
- Account concurrency tests
- Refresh token tests
- GitHub Actions CI
- PostgreSQL service inside CI

---

## 🛠 Tech Stack

| Technology | Usage |
|---|---|
| Java 21 | Application language |
| Spring Boot 4.1 | Application framework |
| Spring Web MVC | REST API |
| Spring Data JPA | Persistence |
| Spring Security | Authentication & authorization |
| JJWT | JWT access tokens |
| PostgreSQL 17 | Database |
| Flyway | Database migrations |
| Maven | Build & dependency management |
| Docker Compose | Local PostgreSQL |
| JUnit / Mockito / MockMvc | Testing |
| GitHub Actions | CI |
| Springdoc OpenAPI | Swagger documentation |

---

## 🔐 Authentication Flow

### Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "fullName": "John Doe",
  "nationalId": "11111111111",
  "phone": "5551234567",
  "email": "john@example.com",
  "password": "12345678"
}
```

On successful registration:

1. Customer is created.
2. A unique **8-digit customer number** is generated.
3. Password is stored using BCrypt.
4. Customer receives the default `USER` role.
5. A default bank account is created automatically with `0.00` balance.
6. Access and refresh tokens are returned.

Example response:

```json
{
  "token": "ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "customerNumber": "42267099"
  }
}
```

### Login

Customers log in with either their **customer number** or **National ID**.

```http
POST /api/auth/login
```

Using customer number:

```json
{
  "identifier": "42267099",
  "password": "12345678"
}
```

Using National ID:

```json
{
  "identifier": "11111111111",
  "password": "12345678"
}
```

Email is kept as a unique contact field and is **not used as the primary login identifier**.

### Refresh Token

```http
POST /api/auth/refresh
```

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

Refresh tokens use rotation and are stored as SHA-256 hashes in the database.

---

## 💳 Main API Endpoints

Authentication is required unless stated otherwise.

### Authentication

```text
POST   /api/auth/register      Public
POST   /api/auth/login         Public
POST   /api/auth/refresh       Public
```

### Accounts

```text
POST   /api/accounts
GET    /api/accounts/me
GET    /api/accounts/{id}
POST   /api/accounts/{id}/deposit
POST   /api/accounts/{id}/withdraw
POST   /api/accounts/transfer
```

`GET /api/accounts/me` returns only the accounts belonging to the authenticated customer.

### Transactions

```text
GET    /api/transactions/account/{accountId}
```

Customers can access transaction history only for accounts they own.

### User Management

```text
GET     /api/users
GET     /api/users/{id}
POST    /api/users
PUT     /api/users/{id}
PATCH   /api/users/{id}
DELETE  /api/users/{id}
```

User management endpoints require `ROLE_ADMIN`.

---

## ⚙️ Environment Variables

The application expects:

```text
DB_PASSWORD
JWT_SECRET
```

Example PowerShell session:

```powershell
$env:DB_PASSWORD="your_database_password"
$env:JWT_SECRET="your_base64_jwt_secret"
```

Sensitive values should never be committed to the repository.

---

## 🐘 Run Locally

### 1. Start PostgreSQL

```bash
docker compose up -d
```

Default local database:

```text
Database: banking_db
User: banking_user
Port: 5432
```

### 2. Run the application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

The API runs by default on:

```text
http://localhost:8080
```

Flyway migrations are applied automatically at startup.

---

## 📖 Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

---

## 🧪 Run Tests

Windows:

```powershell
.\mvnw.cmd test
```

macOS / Linux:

```bash
./mvnw test
```

The repository includes automated unit, integration, security and concurrency tests. GitHub Actions runs the test suite automatically on pushes and pull requests to `main`.

---

## 🔒 Concurrency & Data Integrity

Financial operations use pessimistic database locking where required.

This protects account balances when multiple requests attempt to update the same account concurrently.

Transfers lock accounts in deterministic order to reduce deadlock risk while preserving balance consistency.

---

## 🗄 Database Migrations

Database schema changes are managed with Flyway.

Migration files are located at:

```text
src/main/resources/db/migration
```

The schema includes users, customer identity fields, accounts, transactions, roles and secure refresh-token storage.

---

## 📌 Current Status

```text
Customer Registration                 ✅
Customer Number Generation            ✅
National ID / Customer Number Login   ✅
JWT Authentication                    ✅
Refresh Token Rotation                ✅
Role-Based Authorization              ✅
Automatic Default Account             ✅
Account Ownership                     ✅
Deposit / Withdraw                    ✅
Transfers                             ✅
Transaction History                   ✅
Concurrency Protection                ✅
Flyway Migrations                     ✅
Automated Tests                       ✅
GitHub Actions CI                     ✅
```

### Next Focus

- Connect the Flutter application to real account data
- Replace dashboard mock balances with `/api/accounts/me`
- Display real transaction history
- Improve account model with banking-specific account metadata
- Continue production hardening as the project grows

> This project is built for learning and portfolio purposes and is not intended for production banking use.
