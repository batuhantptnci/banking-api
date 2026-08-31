# Banking API 🏦

**Java 21** • **Spring Boot 4.1.0** • **PostgreSQL 17** • **JWT** • **Flyway** • **Docker**

![CI](../../actions/workflows/ci.yml/badge.svg)

A portfolio-grade banking backend application built with **Java 21, Spring Boot, PostgreSQL, Spring Security, JWT, Flyway and Docker**.

The project demonstrates real-world backend concepts including secure customer authentication, role-based authorization, account ownership, money transfers, transaction history, database migrations, concurrency protection, refresh token rotation and automated CI testing.

---

## 🚀 Features

### 🔐 Authentication & Security

- Customer registration
- Login with **8-digit customer number**
- Login with **11-digit Turkish National ID**
- Automatically generated unique customer number
- BCrypt password hashing
- JWT access token authentication
- Refresh token authentication
- Refresh token rotation
- Refresh token reuse prevention
- SHA-256 hashed refresh token storage
- Concurrent refresh token protection
- Stateless Spring Security
- Role-Based Authorization
- `USER` and `ADMIN` roles
- Admin-only user management endpoints

### 👤 Customer Management

- Full name validation
- National ID validation
- Phone number validation
- Email validation
- Unique customer number
- Unique National ID
- Unique phone number
- Unique email
- Automatic `USER` role assignment
- Admin-protected user management

### 💳 Account Management

- Automatic default account creation after registration
- Automatically generated account numbers
- New accounts start with `0.00` balance
- Create additional bank accounts
- View authenticated customer's accounts
- View account details
- Account ownership validation
- Deposit money
- Withdraw money
- Balance validation
- Pessimistic database locking

### 💸 Transactions

- Deposit
- Withdraw
- Transfer between accounts
- Transaction history
- Sender ownership validation
- Insufficient balance protection
- Concurrent transaction protection
- Deterministic account locking during transfers

### 🗄 Database

- PostgreSQL 17
- Spring Data JPA
- Hibernate
- Flyway migrations
- Database constraints
- Unique indexes
- Foreign keys
- Transaction indexes
- Schema validation

### 🧪 Testing & CI

- Unit tests
- Controller tests
- Service tests
- Integration tests
- Security tests
- Role authorization tests
- Account concurrency tests
- Refresh token tests
- Refresh token rotation tests
- Refresh token concurrency tests
- GitHub Actions CI
- PostgreSQL service inside CI

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
│   ├── AccountController
│   └── TransactionController
│
├── dto
│   ├── AuthResponse
│   ├── CreateUserRequest
│   ├── LoginRequest
│   ├── RefreshTokenRequest
│   ├── AccountResponse
│   ├── DepositRequest
│   ├── WithdrawRequest
│   ├── TransferRequest
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
  "nationalId": "11111111111",
  "phone": "5551234567",
  "email": "john@example.com",
  "password": "12345678"
}
```

During registration:

```text
Register Request
      ↓
Validate Customer Data
      ↓
Generate 8-Digit Customer Number
      ↓
Hash Password with BCrypt
      ↓
Create USER
      ↓
Create Default Bank Account
      ↓
Initial Balance = 0.00
      ↓
Generate Access Token
      ↓
Generate Refresh Token
```

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

New customers automatically receive:

```text
Role: USER
Default Account: ACC-XXXXXXXX
Balance: 0.00
```

Customers cannot assign themselves the `ADMIN` role during registration.

---

## Login

```http
POST /api/auth/login
```

Customers can authenticate using either:

```text
8-digit Customer Number

or

11-digit Turkish National ID
```

### Login with Customer Number

```json
{
  "identifier": "42267099",
  "password": "12345678"
}
```

### Login with National ID

```json
{
  "identifier": "11111111111",
  "password": "12345678"
}
```

Authentication flow:

```text
Customer Number / National ID
            ↓
       Find Customer
            ↓
      Verify Password
            ↓
       Generate JWT
            ↓
   Generate Refresh Token
```

Email remains a unique customer contact field but is **not used as the primary login identifier**.

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

Successful refresh:

```text
Refresh Token
      ↓
Validate
      ↓
Delete Old Token
      ↓
Generate New Access Token
      ↓
Generate New Refresh Token
```

Refresh tokens use **rotation**.

After a refresh token is successfully used, the previous token becomes invalid.

Raw refresh tokens are not stored directly in PostgreSQL.

```text
Raw Refresh Token
      ↓
SHA-256
      ↓
token_hash
      ↓
PostgreSQL
```

This reduces the impact of refresh-token data exposure.

---

# 🛡 Role-Based Authorization

The application supports:

```text
USER
ADMIN
```

Spring Security authorization:

```text
/api/auth/**       → PUBLIC

/swagger-ui/**     → PUBLIC

/v3/api-docs/**    → PUBLIC

/api/users/**      → ROLE_ADMIN

Other API
endpoints          → AUTHENTICATED
```

Normal customers cannot access administrator user-management endpoints.

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

## Automatic Default Account

A default account is automatically created when a new customer registers.

```text
Customer Registration
        ↓
User Created
        ↓
Default Account Created
        ↓
ACC-XXXXXXXX
        ↓
Balance = 0.00
```

Example:

```text
Customer Number: 42267099
Account Number:  ACC-5EA32773
Balance:         0.00
```

---

## Create Additional Account

```http
POST /api/accounts
```

The account is automatically associated with the authenticated customer.

---

## My Accounts

```http
GET /api/accounts/me
```

Example response:

```json
[
  {
    "id": 1,
    "accountNumber": "ACC-5EA32773",
    "balance": 0.00,
    "userId": 2
  }
]
```

The backend determines the authenticated customer from the JWT.

The client does not need to send a user ID.

---

## Get Account

```http
GET /api/accounts/{id}
```

Customers can access only accounts they own.

---

# 💰 Deposit

```http
POST /api/accounts/{id}/deposit
```

Flow:

```text
Authenticated Customer
        ↓
Account Ownership
        ↓
Pessimistic Lock
        ↓
Update Balance
        ↓
Create DEPOSIT Transaction
```

---

# 💸 Withdraw

```http
POST /api/accounts/{id}/withdraw
```

The API verifies:

```text
Authenticated Customer
        ↓
Account Ownership
        ↓
Pessimistic Lock
        ↓
Balance Check
        ↓
Withdraw
        ↓
Create WITHDRAW Transaction
```

A withdrawal cannot reduce the account balance below zero.

---

# 🔁 Transfer

```http
POST /api/accounts/transfer
```

Money can be transferred between accounts.

Transfer flow:

```text
Transfer Request
      ↓
Determine Lock Order
      ↓
Lock Both Accounts
      ↓
Validate Sender Ownership
      ↓
Validate Balance
      ↓
Update Sender Balance
      ↓
Update Receiver Balance
      ↓
Create TRANSFER Transaction
```

Accounts are locked in deterministic ID order to reduce deadlock risk.

---

# 📜 Transaction History

```http
GET /api/transactions/account/{accountId}
```

Authenticated customers can access transaction history only for accounts they own.

Supported transaction types:

```text
DEPOSIT
WITHDRAW
TRANSFER
```

Transaction history is ordered by creation time.

---

# ⚡ Concurrency Protection

Financial balances must remain correct even when multiple requests arrive at the same time.

Critical balance operations use:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

Example scenario:

```text
Starting Balance: 1000

Request A → Withdraw 800
Request B → Withdraw 800
```

Expected result:

```text
One withdrawal succeeds ✅

One withdrawal fails ✅

Final balance = 200 ✅
```

The same principle is used to protect refresh token rotation from concurrent reuse.

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
V6__add_customer_identity.sql
```

### V1

Creates the initial banking tables:

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

Adds role support to users:

```text
USER
ADMIN
```

### V4

Creates:

```text
refresh_tokens
```

### V5

Migrates refresh token storage:

```text
Raw Token
    ↓
SHA-256
    ↓
token_hash
```

### V6

Adds customer identity fields:

```text
customer_number
national_id
phone
```

and unique indexes for:

```text
customer_number
national_id
phone
```

---

# 🧪 Testing

The project contains automated tests covering:

- User service
- User controller
- Authentication
- Customer number / National ID login
- JWT security
- Role authorization
- Account creation
- Automatic default account creation
- Deposits
- Withdrawals
- Transfers
- Transaction history
- Account ownership
- Database migrations
- Concurrent withdrawals
- Refresh token validation
- Refresh token rotation
- Refresh token reuse rejection
- Concurrent refresh requests
- Refresh token SHA-256 storage

Run all tests on Windows:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

The README intentionally does not hardcode the number of tests because the test suite continues to grow.

---

# 🔄 CI/CD

The project uses **GitHub Actions**.

Workflow:

```text
.github/workflows/ci.yml
```

Pipeline:

```text
GitHub Actions
      ↓
Java 21
      ↓
PostgreSQL 17
      ↓
Flyway Migrations
      ↓
Spring Boot Context
      ↓
Automated Tests
      ↓
BUILD SUCCESS ✅
```

The CI environment uses a separate test database.

Sensitive values such as database passwords and JWT secrets are stored using GitHub Actions secrets.

---

# ⚙️ Environment Variables

The application expects:

```text
DB_PASSWORD
JWT_SECRET
```

Example PowerShell configuration:

```powershell
$env:DB_PASSWORD="your_database_password"
$env:JWT_SECRET="your_base64_jwt_secret"
```

Sensitive values must not be committed to the repository.

---

# 🐘 PostgreSQL

Default local configuration:

```text
Database: banking_db
User: banking_user
Port: 5432
```

The database can be inspected using tools such as **DBeaver**.

Start PostgreSQL with Docker Compose:

```bash
docker compose up -d
```

---

# ▶️ Run Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Default API address:

```text
http://localhost:8080
```

Flyway migrations run automatically when the application starts.

---

# 📖 Swagger

When the application is running:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

---

# 🗺 Roadmap

Completed:

- [x] User CRUD
- [x] Request validation
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
- [x] Customer Number Generation
- [x] National ID Authentication
- [x] Customer Number Authentication
- [x] Automatic Default Account Creation
- [x] Unit Tests
- [x] Integration Tests
- [x] GitHub Actions CI

Current focus:

- [ ] Connect Flutter dashboard to `/api/accounts/me`
- [ ] Replace mock balance with real database balance
- [ ] Display real account number in Flutter
- [ ] Connect real transaction history

Possible future improvements:

- [ ] Logout / Refresh Token Revocation
- [ ] Account type support
- [ ] Currency support
- [ ] IBAN support
- [ ] Account status management
- [ ] Transaction limits
- [ ] Audit logging
- [ ] Pagination
- [ ] API versioning
- [ ] Rate limiting
- [ ] Production configuration

---

# 📌 Current Status

```text
Customer Registration                  ✅
8-Digit Customer Number Generation     ✅
National ID / Customer Number Login    ✅
Password Hashing                       ✅
JWT Authentication                     ✅
Refresh Token Rotation                 ✅
Hashed Refresh Token Storage           ✅
Role-Based Authorization               ✅
User Management                        ✅
Automatic Default Account              ✅
Accounts                               ✅
Deposits                               ✅
Withdrawals                            ✅
Transfers                              ✅
Transaction History                    ✅
Account Ownership                      ✅
Concurrency Protection                 ✅
Flyway Migrations                      ✅
Automated Tests                        ✅
GitHub Actions CI                      ✅
```

The backend core is now ready to provide real banking data to the Flutter application.

Next development focus:

```text
PostgreSQL
    ↓
Real Account
    ↓
GET /api/accounts/me
    ↓
Flutter
    ↓
Real Dashboard
```

> This project is developed for learning and portfolio purposes and is not intended for real-world production banking use.