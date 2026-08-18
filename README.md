<div align="center">

# 🏦 Banking API

### Secure Banking Backend built with Java & Spring Boot

A portfolio banking backend featuring **JWT Authentication, secure account operations, money transfers and transaction history.**

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-purple)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)

</div>

---

## ✨ Features

### 🔐 Authentication & Security

- ✅ User Registration
- ✅ User Login
- ✅ BCrypt Password Hashing
- ✅ JWT Token Generation
- ✅ JWT Token Validation
- ✅ Stateless Authentication
- ✅ Protected API Endpoints
- ✅ Account Ownership Control
- ✅ `401 Unauthorized` handling
- ✅ `403 Forbidden` handling

---

### 👤 User Management

- ✅ Create User
- ✅ Get User
- ✅ Update User
- ✅ Delete User
- ✅ Email Validation
- ✅ Duplicate Email Protection
- ✅ Request / Response DTOs

---

### 💳 Bank Accounts

- ✅ Create Bank Account
- ✅ Automatic Account Number Generation
- ✅ View My Accounts
- ✅ View Specific Account
- ✅ Account Ownership Validation
- ✅ Secure Account Access

Example account number:

```text
ACC-A1602ADE
```

---

### 💸 Banking Operations

- ✅ Deposit Money
- ✅ Withdraw Money
- ✅ Transfer Money
- ✅ Balance Validation
- ✅ Insufficient Balance Protection
- ✅ Same Account Transfer Protection
- ✅ Secure Sender Ownership Check
- ✅ Database Transaction Management with `@Transactional`

---

### 📜 Transaction History

- ✅ Deposit History
- ✅ Withdrawal History
- ✅ Transfer History
- ✅ Incoming Transactions
- ✅ Outgoing Transactions
- ✅ Account-Based Transaction History
- ✅ Newest Transactions First
- ✅ Secure Transaction Access

Example:

```json
{
  "type": "TRANSFER",
  "direction": "OUTGOING",
  "amount": 250.00,
  "accountId": 6,
  "targetAccountId": 2
}
```

---

# 🔐 Authentication Flow

```text
        REGISTER / LOGIN
               │
               ▼
        Email + Password
               │
               ▼
        BCrypt Validation
               │
               ▼
          JWT Generated
               │
               ▼
 Authorization: Bearer <TOKEN>
               │
               ▼
          JwtAuthFilter
               │
               ▼
       Spring Security
               │
               ▼
       Protected API 🔒
```

---

# 🚀 API Overview

## 🔓 Authentication

### Register

```http
POST /api/auth/register
```

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

### Login

```http
POST /api/auth/login
```

```json
{
  "email": "john@example.com",
  "password": "Password123"
}
```

Successful authentication:

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

---

## 🔒 Account Operations

> All endpoints below require a valid JWT.

### 🆕 Create Account

```http
POST /api/accounts
```

The account is automatically created for the authenticated user.

No `userId` is accepted from the client. 🔐

---

### 👀 My Accounts

```http
GET /api/accounts/me
```

---

### 💰 Deposit

```http
POST /api/accounts/{accountId}/deposit
```

```json
{
  "amount": 500
}
```

---

### 💸 Withdraw

```http
POST /api/accounts/{accountId}/withdraw
```

```json
{
  "amount": 100
}
```

---

### 🔄 Transfer

```http
POST /api/accounts/transfer
```

```json
{
  "fromAccountId": 6,
  "toAccountId": 2,
  "amount": 250
}
```

✅ Sender account must belong to the authenticated user.

✅ Receiver account may belong to another user.

---

## 📜 Transaction History

```http
GET /api/transactions/account/{accountId}
```

Users can only view transactions belonging to their own accounts.

---

# 🛡️ Security Rules

```text
❌ Plain text passwords are never stored
✅ Passwords are hashed with BCrypt

❌ JWT secret is never committed
✅ JWT secret comes from environment variables

❌ Clients cannot choose another user's account
✅ Account ownership comes from authenticated JWT identity

❌ Users cannot withdraw from another user's account
✅ Ownership validation runs before banking operations
```

---

# ⚙️ Environment Variables

```text
DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_base64_jwt_secret
```

⚠️ Never commit real secrets to GitHub.

---

# 🧱 Project Architecture

```text
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
PostgreSQL
```

Additional layers:

```text
DTO
Mapper
Exception Handling
Security
JWT Filter
```

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| ☕ Java 21 | Backend Language |
| 🍃 Spring Boot | Application Framework |
| 🔐 Spring Security | Authentication & Authorization |
| 🪪 JWT | Token Authentication |
| 🔒 BCrypt | Password Hashing |
| 🐘 PostgreSQL | Database |
| 🗄️ Spring Data JPA | ORM / Persistence |
| ✅ Jakarta Validation | Request Validation |
| 🐳 Docker | Database / Containerization |
| 📦 Maven | Dependency Management |
| ✨ Lombok | Boilerplate Reduction |

---

# 📂 Project Structure

```text
com.batuhan.bankingapi
│
├── 📁 config
│   ├── JwtAuthFilter
│   └── SecurityConfig
│
├── 📁 controller
│   ├── AuthController
│   ├── AccountController
│   ├── TransactionController
│   └── UserController
│
├── 📁 dto
├── 📁 entity
├── 📁 exception
├── 📁 mapper
├── 📁 repository
│
└── 📁 service
    ├── AccountService
    ├── AuthService
    ├── JwtService
    ├── TransactionService
    └── UserService
```

---

# 🗺️ Roadmap

### Backend

- [x] User CRUD
- [x] Account Management
- [x] Deposit
- [x] Withdraw
- [x] Transfer
- [x] Transaction History
- [x] BCrypt Authentication
- [x] JWT Authentication
- [x] Account Authorization
- [ ] Unit Tests
- [ ] Integration Tests
- [ ] Swagger / OpenAPI
- [ ] Database Migrations
- [ ] Docker Compose
- [ ] Concurrency / Account Locking
- [ ] Refresh Tokens
- [ ] CI/CD with GitHub Actions

### 📱 Mobile App

- [ ] Flutter Project
- [ ] Register Screen
- [ ] Login Screen
- [ ] Secure Token Storage
- [ ] Home Dashboard
- [ ] Account Details
- [ ] Deposit / Withdraw
- [ ] Money Transfer
- [ ] Transaction History
- [ ] Profile

---

<div align="center">

## 🚧 Project Status

### Backend development is actively continuing.

Current focus:

**Authentication ✅ → Security ✅ → Testing 🧪**

---

Built with ☕ Java & 🍃 Spring Boot

</div>