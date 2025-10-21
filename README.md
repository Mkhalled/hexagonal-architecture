# Hexagonal Architecture Spring Boot Demo

A comprehensive demonstration of **Hexagonal Architecture (Ports & Adapters Pattern)** using Spring Boot 3.2.0 and Spring Cloud 2023.0.0.

This project showcases how to build scalable, maintainable, and testable applications by keeping business logic completely independent from external frameworks and infrastructure concerns.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Module Descriptions](#module-descriptions)
4. [Getting Started](#getting-started)
5. [API Usage](#api-usage)
6. [Key Technologies](#key-technologies)
7. [Project Structure](#project-structure)
8. [Design Patterns](#design-patterns)

---

## Project Overview

This project implements a **Product Management System** using the Hexagonal Architecture pattern. The system demonstrates:

- ✅ **Clean Separation of Concerns** - Each layer has a single responsibility
- ✅ **Framework Agnostic Domain** - Business logic doesn't depend on Spring
- ✅ **Testability** - Pure domain logic tested without framework overhead
- ✅ **Flexible Adapters** - Easy to swap implementations (Database, APIs, etc.)
- ✅ **API Layer Isolation** - DTOs keep API concerns separate from domain

---

## Architecture

### Hexagonal Architecture Pattern

The project follows the **Hexagonal Architecture** (also called **Ports & Adapters**), which organizes code into three main layers:

```
┌─────────────────────────────────────────────────────────┐
│                      EXPOSITION LAYER                    │
│  (REST Controllers, DTOs, JSON Serialization)            │
├─────────────────────────────────────────────────────────┤
│                    APPLICATION LAYER                     │
│  (Use Cases, Orchestration, Business Workflows)          │
├─────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                        │
│  (Pure Business Logic, Entities, Validation)             │
├─────────────────────────────────────────────────────────┤
│                   INFRASTRUCTURE LAYER                   │
│  (Persistence, Security, Configuration, Adapters)       │
└─────────────────────────────────────────────────────────┘
```

### Data Flow

```
HTTP Request
    ↓
ProductController (Boot) - Accepts ProductDTO
    ↓
ProductUseCase (Application) - Orchestrates
    ↓
ProductServiceImpl (Domain) - Executes business logic
    ↓
ProductPersistenceAdapter (Infrastructure) - Calls JPA
    ↓
JpaProductRepository (Spring Data) - Database Access
    ↓
ProductMapper - Converts between layers
    ↓
HTTP Response (ProductDTO)
```

---

## Module Descriptions

### 1. **hexagonal-domain** - Pure Business Logic Layer

**Purpose**: Contains all business rules and domain logic, completely independent of any framework.

**Key Components**:

- **`Product.java`** - Domain entity representing a product

  - Immutable data model
  - Contains only business-relevant properties (id, name, description, price, quantity)
  - Uses Lombok `@Builder` for object construction

- **`ProductServiceImpl.java`** - Domain service implementing business logic

  - `createProduct()` - Validates and creates products
  - `getProduct()` - Retrieves a product
  - `getAllProducts()` - Lists all products
  - `updateProduct()` - Updates product with validation
  - `deleteProduct()` - Removes a product
  - All methods delegate to injected `ProductRepository` port

- **`ProductService` (Port)** - Interface defining the domain contract

  - Defines what operations the domain can perform
  - Implemented by `ProductServiceImpl`
  - Agnostic to Spring or any framework

- **`ProductRepository` (Port)** - Interface for data persistence

  - Defines data access contract: `save()`, `findById()`, `findAll()`, `deleteById()`
  - No Spring Data annotations - pure interface
  - Implemented by `ProductPersistenceAdapter` in infrastructure

- **`BusinessValidationException`** - Custom domain exception

  - Thrown when business rules are violated
  - Handled by global exception handler in boot layer

- **`ResourceNotFoundException`** - Custom domain exception
  - Thrown when resource not found

**Dependencies**:

- Only JUnit and Mockito for testing
- **Zero Spring dependencies** ✅

**Testing**: `ProductServiceImplTest.java`

- 7 comprehensive unit tests
- Tests business logic without Spring context
- Mocks `ProductRepository` port

---

### 2. **hexagonal-application** - Orchestration Layer

**Purpose**: Implements use cases by orchestrating domain services and coordinating business flows.

**Key Components**:

- **`ProductUseCase.java`** - Application service
  - Annotated with `@Service` for Spring component scanning
  - Implements `ProductService` interface
  - **Delegates to domain `ProductServiceImpl`**
  - Provides a bridge between REST controllers and domain services
  - Handles cross-cutting concerns that may be needed in future

**Dependencies**:

- hexagonal-domain module
- spring-context (for @Service annotation)
- JUnit, Mockito for testing

**Testing**: `ProductUseCaseTest.java`

- Verifies delegation to domain service
- Mocks `ProductService` port

---

### 3. **hexagonal-infrastructure** - Technical Adapters Layer

**Purpose**: Implements technical concerns like persistence, security, configuration, and external integrations.

**Key Components**:

#### **Persistence Adapter**

- **`ProductPersistenceAdapter.java`** - Implements `ProductRepository` port

  - Annotated with `@Component` for Spring
  - Wraps `JpaProductRepository` (Spring Data JPA)
  - Converts between `Product` (domain) ↔ `ProductEntity` (JPA)

- **`ProductEntity.java`** - JPA persistence model

  - Maps to database table `products`
  - Separate from domain `Product`

- **`JpaProductRepository`** - Spring Data JPA interface

#### **Mapping & DTO**

- **`ProductMapper.java`** - MapStruct mapper

  - Converts: Entity ↔ Domain ↔ DTO
  - Compile-time safe mappings

- **`ProductDTO.java`** - Data Transfer Object
  - Used for API request/response
  - Shields domain model from API concerns

#### **Security**

- **`ApiKeyAuthFilter.java`** - Custom security filter

  - Checks `X-API-KEY` header on every request
  - Exempts Swagger UI (`/swagger-ui`, `/v3/api-docs`)

- **`ApiKeyProperties.java`** - Configuration properties

#### **Configuration**

- **`SecurityConfig.java`** - Spring Security configuration
- **`OpenApiConfig.java`** - Swagger/OpenAPI configuration
- **`GlobalExceptionHandler.java`** - Centralized exception handling

**Testing**:

- `ApiKeyAuthFilterTest.java` - 7 tests
- `ProductPersistenceAdapterTest.java` - 7 tests
- `ProductMapperTest.java` - 11 tests

---

### 4. **hexagonal-boot** - Entry Point & Exposition Layer

**Purpose**: Application bootstrap and REST endpoint exposure.

**Key Components**:

- **`HexagonalApplication.java`** - Spring Boot main class

  - `@SpringBootApplication(scanBasePackages = "com.hexagonal.demo")`

- **`ProductController.java`** - REST API endpoints

  - Accepts & returns `ProductDTO` (not domain `Product`)
  - Injects `ProductService` via `@Autowired`
  - Endpoints:
    - `POST /api/products` - Create
    - `GET /api/products/{id}` - Get by ID
    - `GET /api/products` - List all
    - `PUT /api/products/{id}` - Update
    - `DELETE /api/products/{id}` - Delete

- **`application-dev.yml`** - Development configuration

**Dependencies**:

- hexagonal-application, hexagonal-infrastructure
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- springdoc-openapi-starter-webmvc-ui

**Testing**: `ProductControllerTest.java`

- 6 integration tests with MockMvc

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8.1+
- PostgreSQL 12+ (localhost:5432)

### 1. Create Database

```bash
createdb hexagonal_db
```

### 2. Build

```bash
mvn clean install
```

### 3. Run

```bash
mvn spring-boot:run -pl hexagonal-boot
```

Application starts on `http://localhost:8080/api`

### 4. Access Swagger UI

```
http://localhost:8080/api/swagger-ui.html
```

---

## API Usage

### Headers Required

All endpoints require:

```
X-API-Key: ma_cle_api_test
```

### Create Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ma_cle_api_test" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "quantity": 5
  }'
```

### Get All Products

```bash
curl -X GET http://localhost:8080/api/products \
  -H "X-API-Key: ma_cle_api_test"
```

### Get Product by ID

```bash
curl -X GET http://localhost:8080/api/products/1 \
  -H "X-API-Key: ma_cle_api_test"
```

### Update Product

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ma_cle_api_test" \
  -d '{
    "name": "Updated Laptop",
    "price": 1199.99
  }'
```

### Delete Product

```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "X-API-Key: ma_cle_api_test"
```

---

## Key Technologies

| Technology        | Version  | Purpose               |
| ----------------- | -------- | --------------------- |
| Java              | 17       | Programming Language  |
| Spring Boot       | 3.2.0    | Framework             |
| Spring Cloud      | 2023.0.0 | Distributed Systems   |
| Spring Data JPA   | 6.1.1    | ORM                   |
| Hibernate         | 6.3.1    | JPA Implementation    |
| PostgreSQL        | 12+      | Database              |
| Liquibase         | 4.x      | Schema Migration      |
| MapStruct         | 1.5.5    | Bean Mapping          |
| Lombok            | Latest   | Boilerplate Reduction |
| SpringDoc OpenAPI | 2.3.0    | Swagger/OpenAPI       |
| JUnit 5           | 5.10.2   | Testing               |
| Mockito           | 5.2.0    | Mocking               |

---

## Project Structure

```
hexagonal-architecture/
├── hexagonal-domain/              # Business Logic (No Spring)
│   ├── model/Product.java
│   ├── ports/
│   │   ├── ProductService.java
│   │   └── ProductRepository.java
│   ├── service/ProductServiceImpl.java
│   ├── exception/
│   └── test/ProductServiceImplTest.java
│
├── hexagonal-application/         # Orchestration (@Service)
│   ├── service/ProductUseCase.java
│   └── test/ProductUseCaseTest.java
│
├── hexagonal-infrastructure/      # Adapters & Config
│   ├── adapters/output/persistence/
│   │   ├── ProductPersistenceAdapter.java
│   │   ├── entity/ProductEntity.java
│   │   ├── mapper/ProductMapper.java
│   │   └── repository/JpaProductRepository.java
│   ├── config/
│   │   ├── security/ApiKeyAuthFilter.java
│   │   ├── SecurityConfig.java
│   │   ├── OpenApiConfig.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/ProductDTO.java
│   └── test/
│       ├── ApiKeyAuthFilterTest.java
│       ├── ProductPersistenceAdapterTest.java
│       └── ProductMapperTest.java
│
├── hexagonal-boot/               # Entry Point
│   ├── HexagonalApplication.java
│   ├── controller/ProductController.java
│   ├── resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── db/changelog/
│   └── test/ProductControllerTest.java
│
├── pom.xml
└── README.md
```

---

## Design Patterns Used

1. **Hexagonal Architecture** - Ports & Adapters pattern
2. **Repository Pattern** - Abstract persistence
3. **Data Transfer Object (DTO)** - API model separation
4. **Mapper Pattern** - Type-safe conversions (MapStruct)
5. **Strategy Pattern** - Service implementations
6. **Adapter Pattern** - Technical integrations
7. **Filter Pattern** - Security filtering
8. **Configuration Pattern** - Externalized config

---

## Testing Strategy

### Domain Layer (No Spring) ✅

- **ProductServiceImplTest.java** (7 tests)
- Fast execution
- Pure business logic

### Application Layer ✅

- **ProductUseCaseTest.java**
- Tests delegation to domain

### Infrastructure Layer ✅

- **ApiKeyAuthFilterTest.java** (7 tests)
- **ProductPersistenceAdapterTest.java** (7 tests)
- **ProductMapperTest.java** (11 tests)

### Boot Layer ✅

- **ProductControllerTest.java** (6 tests)
- Integration tests with Spring

**Run Tests**:

```bash
mvn test
mvn test -pl hexagonal-domain
```

---

## Clean Architecture Benefits

✅ **Testability** - Domain tested without Spring
✅ **Maintainability** - Clear separation of concerns
✅ **Flexibility** - Easy to swap adapters
✅ **Scalability** - Add use cases without impacting existing code
✅ **Framework Agnostic** - Domain is framework-independent

---

## Contributing

1. Follow the architectural layers
2. Keep domain logic framework-independent
3. Write unit tests for domain logic
4. Use DTOs for API boundaries
5. Run `mvn clean install` before committing

---

## License

MIT License

---

**Happy Coding! 🚀**
