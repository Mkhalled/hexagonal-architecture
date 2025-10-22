# Hexagonal Infrastructure Module

Adapters, configuration, and external dependencies - **Spring integration hub**.

## Quick Overview

| Aspect            | Details                                                     |
| ----------------- | ----------------------------------------------------------- |
| **Purpose**       | Implement domain ports, configure Spring, handle exceptions |
| **Adapters**      | Database, Security, OpenAPI, Persistence                    |
| **Configuration** | GlobalExceptionHandler, SecurityConfig, OpenApiConfig       |
| **Key Classes**   | ProductPersistenceAdapter, ProductMapper, ProductEntity     |

## Structure

```
infrastructure/
├── config/
│   ├── SecurityConfig.java           (X-API-Key auth)
│   ├── OpenApiConfig.java            (Swagger/OpenAPI)
│   └── ProductServiceConfig.java     (Bean definitions)
├── adapter/
│   ├── persistence/
│   │   ├── ProductPersistenceAdapter.java  (implements ProductRepository)
│   │   ├── ProductMapper.java              (Entity ↔ Domain mapping)
│   │   ├── ProductEntity.java              (JPA entity)
│   │   └── ProductJpaRepository.java       (Spring Data)
│   └── security/
│       ├── ApiKeyAuthFilter.java    (X-API-Key validation)
│       └── SecurityConstants.java
├── error/
│   └── GlobalExceptionHandler.java  (Exception → Response mapping)
└── test/
    ├── GlobalExceptionHandlerTest.java   (9 tests)
    └── ApiKeyAuthFilterTest.java         (8 parameterized tests)
```

## Key Components

### 1. Persistence Adapter

**Implements** `ProductRepository` (domain SPI port):

```java
@Component
public class ProductPersistenceAdapter implements ProductRepository {
    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) { ... }

    @Override
    public Optional<Product> findById(Long id) { ... }
}
```

### 2. Entity Mapping

```
Domain Model (Product)  ←→  JPA Entity (ProductEntity)
                             ↓
                        Database (PostgreSQL)
```

`ProductMapper`: Converts between layers

### 3. Security Configuration

- **X-API-Key** authentication filter
- Exempts: `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/**`
- Validates all other endpoints

### 4. Exception Handling

`GlobalExceptionHandler` catches:

- `BusinessValidationException` → 400 Bad Request
- `ResourceNotFoundException` → 404 Not Found
- Validation errors → 400 + field details
- Generic errors → 500 Internal Server Error

### 5. OpenAPI Configuration

- Swagger integration
- API documentation
- Security scheme definition

## Integration Points

| Layer           | How It Connects                                   |
| --------------- | ------------------------------------------------- |
| **Domain**      | Implements ProductRepository, uses ProductService |
| **Application** | Available via Spring dependency injection         |
| **Boot**        | Configuration auto-loaded via Spring              |

## Dependencies

| Dependency        | Type      | Scope   | Why                    |
| ----------------- | --------- | ------- | ---------------------- |
| Domain            | Module    | Compile | Implement domain ports |
| Spring Data JPA   | Framework | Compile | Database operations    |
| Spring Security   | Framework | Compile | API key auth           |
| Spring OpenAPI    | Framework | Compile | API documentation      |
| PostgreSQL Driver | Database  | Runtime | Production DB          |
| H2 Database       | Database  | Test    | Integration tests      |
| JUnit 5           | Test      | Test    | Unit testing           |
| Mockito           | Test      | Test    | Mock Spring components |

## Testing

### Integration Tests with H2

```bash
mvn test -pl hexagonal-infrastructure
```

**Tests cover:**

- GlobalExceptionHandler (9 tests) - all exception scenarios
- ApiKeyAuthFilter (8 parameterized tests) - auth validation & exemptions

### Database

| Environment | DB                             |
| ----------- | ------------------------------ |
| Production  | PostgreSQL localhost:5432      |
| Testing     | H2 in-memory (auto-configured) |

## Configuration

### application.properties (Production)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hexagonal_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### application-test.yml (Testing)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
  liquibase:
    enabled: false
```

## Design Philosophy

✅ **Why separate adapters?**

- Easy to swap implementations (PostgreSQL → MongoDB)
- Infrastructure changes don't affect domain
- Multiple adapters can coexist

✅ **Why global exception handler?**

- Consistent error responses
- Error codes for API clients
- Centralized error mapping

✅ **Why centralized configuration?**

- Single point for Spring setup
- Clear dependencies
- Testable bean creation

## What Not to Do

❌ Don't add business logic here
❌ Don't throw domain exceptions from adapters (wrap & map)
❌ Don't bypass exception handler
❌ Don't access repositories directly from controllers (use use cases)
❌ Don't add HTTP logic to entities

## Extending

Add new adapters:

- Payment Gateway adapter
- Email Service adapter
- Cache adapter
- Message Queue adapter

Pattern: Interface → Implementation → Configuration

---

**Implements:** Domain ports (ProductRepository)

**Used by:** Boot layer, Controllers

**Next module:** [Boot Layer](../hexagonal-boot/README.md)
