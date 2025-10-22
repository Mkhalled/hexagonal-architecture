# 🏗️ Hexagonal Architecture - Spring Boot

A clean, testable implementation of **hexagonal architecture** (ports & adapters) with Spring Boot 3.2.

> **Target Audience:** Developers who need to quickly understand the architecture and start coding.

---

## 🚀 Quick Start (5 min)

### Setup

```bash
# 1. Clone & build
git clone <repo>
cd hexagonal-architecture
mvn clean install

# 2. Run tests
mvn test

# 3. Start server (requires PostgreSQL)
mvn spring-boot:run -pl hexagonal-boot \
  -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# API is at: http://localhost:8080
# Docs: http://localhost:8080/swagger-ui.html
```

### Quick Test

```bash
# Create product
curl -X POST http://localhost:8080/api/products \
  -H "X-API-Key: ma_cle_api_test" \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":1299.99,"quantity":5}'

# Get all products
curl -X GET http://localhost:8080/api/products \
  -H "X-API-Key: ma_cle_api_test"
```

---

## 🎯 Architecture Overview

```
┌──────────────────────────────────────────────┐
│         REST Clients / HTTP                   │
└─────────────────┬──────────────────────────────┘
                  │ REST
┌─────────────────────────────────────────────┐
│  Boot: REST Controllers                      │
│  ProductController.java                      │
└─────────────────┬──────────────────────────────┘
                  │ Spring Autowiring
┌─────────────────────────────────────────────┐
│  Application: Use Cases                      │
│  ProductUseCase.java (orchestration)         │
└─────────────────┬──────────────────────────────┘
                  │ Interface (ProductService)
┌─────────────────────────────────────────────┐
│  Domain: Business Logic (Pure Java)          │
│  ProductServiceImpl.java (core logic)         │
│  Product.java (entity)                       │
└─────────────────┬──────────────────────────────┘
                  │ Interface (ProductRepository)
┌─────────────────────────────────────────────┐
│  Infrastructure: Adapters                    │
│  ProductPersistenceAdapter.java (implements) │
│  ProductEntity.java (JPA)                    │
└─────────────────┬──────────────────────────────┘
                  │ JDBC / SQL
┌─────────────────────────────────────────────┐
│  PostgreSQL Database                         │
└─────────────────────────────────────────────┘
```

### Key Principle: **Dependency Direction**

```
Infrastructure ─→ Application ─→ Domain ← (depends on nothing)
```

✅ Domain has **ZERO Spring dependencies**  
✅ Easy to test (no framework overhead)  
✅ Easy to swap implementations (DB, security, etc.)

---

## 📦 Module Structure

| Module                                                     | Purpose                         | Framework             | Tests          |
| ---------------------------------------------------------- | ------------------------------- | --------------------- | -------------- |
| **[Domain](./hexagonal-domain/README.md)**                 | Pure business logic, validation | None ✅               | 7 unit         |
| **[Application](./hexagonal-application/README.md)**       | Use case orchestration          | Spring (light)        | 1 unit         |
| **[Infrastructure](./hexagonal-infrastructure/README.md)** | Adapters, config, security, DB  | Spring, JPA, Security | 17 integration |
| **[Boot](./hexagonal-boot/README.md)**                     | REST API, controllers, startup  | Spring Boot           | 7 integration  |

---

## 📚 Read Each Module's README

### 1️⃣ [Domain Module](./hexagonal-domain/README.md) — 5 min read

**What:** Core business logic (ZERO Spring)

**Contains:**

- `Product.java` — Domain entity
- `ProductService` — API port (what domain offers)
- `ProductRepository` — SPI port (what domain needs)
- `ProductServiceImpl.java` — Business rules & validation
- Business exceptions

**Why pure?** Fast tests, reusable, framework-agnostic

---

### 2️⃣ [Application Module](./hexagonal-application/README.md) — 3 min read

**What:** Use case orchestration

**Contains:**

- `ProductUseCase.java` — CRUD operations
- Calls domain service
- Handles application-level logic

---

### 3️⃣ [Infrastructure Module](./hexagonal-infrastructure/README.md) — 7 min read

**What:** Adapters & external integrations

**Contains:**

- `ProductPersistenceAdapter` — Implements ProductRepository
- `ProductEntity` — JPA entity mapping
- `ProductMapper` — Domain ↔ DB conversion
- Security filters, error handlers, config
- Global exception handling (400, 403, 404, 500)
- API Key authentication

**Key:** All Spring/JPA code lives here

---

### 4️⃣ [Boot Module](./hexagonal-boot/README.md) — 5 min read

**What:** Application entry point & HTTP layer

**Contains:**

- `ProductController.java` — REST endpoints
- `HexagonalApplication.main()` — Spring Boot startup
- `application-dev.yml` — Database config
- `application-test.yml` — H2 in-memory config

---

## 🔧 Technology Stack

| Layer         | Technology        | Version |
| ------------- | ----------------- | ------- |
| **Language**  | Java              | 17      |
| **Framework** | Spring Boot       | 3.2.0   |
| **Build**     | Maven             | 3.9+    |
| **Database**  | PostgreSQL        | 15+     |
| **Testing**   | JUnit 5, Mockito  | Latest  |
| **ORM**       | JPA/Hibernate     | 6.3+    |
| **Security**  | Spring Security   | 6.1+    |
| **API Doc**   | Springdoc OpenAPI | 2.3+    |

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Module Tests

```bash
mvn test -pl hexagonal-domain      # 7 tests (pure unit)
mvn test -pl hexagonal-application # 1 test
mvn test -pl hexagonal-infrastructure # 17 tests (integration)
mvn test -pl hexagonal-boot        # 7 tests (integration)
```

### Test Profile

- **Dev:** PostgreSQL
- **Test:** H2 in-memory (auto-created)
- **Prod:** PostgreSQL with Liquibase

---

## 📋 API Endpoints

All endpoints require: `-H "X-API-Key: ma_cle_api_test"`

```bash
# Create product
POST   /api/products
Body: {"name":"...", "price":100, "quantity":5}

# Get all
GET    /api/products

# Get one
GET    /api/products/{id}

# Update
PUT    /api/products/{id}
Body: {"name":"...", "price":100, "quantity":5}

# Delete
DELETE /api/products/{id}

# Docs
GET    /swagger-ui.html
GET    /v3/api-docs
```

---

## ⚙️ Configuration

### Development Profile (`application-dev.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hexagonal_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: none
  liquibase:
    enabled: true
```

### Test Profile (`application-test.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  liquibase:
    enabled: false
```

---

## 🎨 Design Patterns Used

| Pattern                          | Where                   | Purpose         |
| -------------------------------- | ----------------------- | --------------- |
| **Hexagonal (Ports & Adapters)** | Global                  | Decouple layers |
| **Repository**                   | Infrastructure          | Data access     |
| **Adapter**                      | Infrastructure          | Implement ports |
| **Builder**                      | Domain model            | Object creation |
| **Dependency Injection**         | Spring                  | Loose coupling  |
| **DTO**                          | Boot layer              | API contracts   |
| **Exception Strategy**           | Domain + Infrastructure | Error handling  |

---

## 🚦 Project Health

```
✅ Build:   PASSING
✅ Tests:   32 PASSING (7 unit + 25 integration)
✅ Quality: Checkstyle configured
✅ Coverage: Domain 100%, Infrastructure 80%+
✅ Docs:    OpenAPI/Swagger enabled
```

---

## 🤔 Common Questions

### **Q: Where do I add a new entity?**

A: Add in Domain module (`model/`), implement ports, then add adapter in Infrastructure

### **Q: How do I change the database?**

A: Only change `ProductPersistenceAdapter` — no other layer affected

### **Q: Can I use this without Spring?**

A: Yes! Domain module has zero Spring dependencies

### **Q: How are tests organized?**

A: Unit tests in each module, integration tests in boot/infrastructure

### **Q: What about DTOs?**

A: They live in Boot layer (REST concerns) — separate from domain

---

## 📖 Further Reading

- [Domain Module Details](./hexagonal-domain/README.md)
- [Application Module Details](./hexagonal-application/README.md)
- [Infrastructure Module Details](./hexagonal-infrastructure/README.md)
- [Boot Module Details](./hexagonal-boot/README.md)

---

## 🎓 Learning Path

1. **Start:** Read this README (you're here!)
2. **Understand:** Read each module's README (20 min total)
3. **Explore:** Browse the code structure
4. **Run:** `mvn clean install && mvn test`
5. **Code:** Add a new feature following the pattern
6. **Master:** Swap infrastructure adapters (PostgreSQL → MongoDB)

---

## 📞 Support

- Check module READMEs first
- Review test files for usage examples
- Look at existing code patterns

---

**Last Updated:** October 2025  
**License:** MIT  
**Contributors:** Development Team
