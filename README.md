# 🧩 Accounts API (Reactive Spring WebFlux + Gradle)

A reactive account services microservice built with **Spring Boot (WebFlux)** and **R2DBC (MySQL)**.
The service provides endpoints for account registration and management, with **JWT-based authentication**, using non-blocking reactive programming.

---

## ✅ Code Coverage
[![codecov](https://codecov.io/github/baxterrp/io.baxter.accounts/branch/main/graph/badge.svg?token=7W4PYZOXFH)](https://codecov.io/github/baxterrp/io.baxter.accounts)

## 📘 API Specification
You can view the full OpenAPI specification here:  
👉 [openapi.json](https://github.com/baxterrp/io.baxter.accounts/blob/main/openapi.json)

## 🚀 Features

- Reactive and non-blocking using **Spring WebFlux**
- JWT authentication for stateless security
- Secure password hashing with **BCrypt**
- Role-based access system (Users ↔ Roles)
- Centralized exception handling for clean API responses
- **MySQL** integration via **R2DBC**
- Containerized using **Docker Compose**
- Configurable via environment variables

---

## 🧱 Project Structure

```
io.baxter.accounts
├── api
│   ├── controllers        # REST endpoints (AccountController)
│   ├── models             # Request/Response DTOs
│   └── services           # AccountService
├── data
│   ├── models             # R2DBC entity models (AccountDataModel, AddressDataModel)
│   └── repository         # Reactive repositories (AccountRepository, AddressRepository, etc.)
├── infrastructure
│   ├── auth               # JWT token generation + password encryption
│   └── behavior           # Exception handling and domain-specific exceptions
├── docker-compose.yml
└── src/main/resources/application.properties
```

---

## ⚙️ Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17+ |
| Framework | Spring Boot 3.5.x (WebFlux) |
| Database | MySQL 8 (Reactive via R2DBC) |
| Security | JWT + BCrypt |
| Build Tool | Gradle Kotlin DSL |
| Reactive Engine | Project Reactor (Mono / Flux) |
| Containerization | Docker & Docker Compose |

---

## 🧠 Configuration

Environment variables are used for flexibility.  
`application.properties` expects:

```
spring.application.name=io.baxter.accounts

spring.r2dbc.url=${SPRING_R2DBC_URL}
spring.r2dbc.username=${SPRING_R2DBC_USERNAME}
spring.r2dbc.password=${SPRING_R2DBC_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration-ms=${JWT_EXPIRATION_MS}
```

---

## 🐳 Running with Docker Compose

1. **Create a `.env` file** in the project root:

```
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=accounts_db
MYSQL_USER=accountuser
MYSQL_PASSWORD=accountpass
MYSQL_PORTS=3306:3306
MYSQL_URL=r2dbc:mysql://db:3306/accounts_db
API_PORTS=8080:8080
JWT_SECRET=supersecretkey
JWT_EXPIRATION_MS=3600000
```

2. **Build and start services:**

```bash
docker compose up --build
```

- MySQL container: `accounts-db`
- API container: `accounts-api`
- API available at: `http://localhost:{port}/api`

---

## 💻 Running Locally (Gradle)

If you prefer to run without Docker:

1. Start a local MySQL database.
2. Set environment variables or edit `application.properties`:

```
spring.r2dbc.url=r2dbc:mysql://localhost:3306/accounts_db
spring.r2dbc.username=authuser
spring.r2dbc.password=authpass

jwt.secret=supersecretkey
jwt.expiration-ms=3600000
```

3. Run the application with Gradle:

```bash
./gradlew bootRun
```

---

### 👤 Author

**Robert Baxter**  
💻 [GitHub](https://github.com/baxterrp)

