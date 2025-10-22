# Hexagonal Infrastructure Module

Adapters, configuration, and external dependencies - **Spring integration hub**.

## Quick Overview

| Aspect            | Details                                                                     |
| ----------------- | --------------------------------------------------------------------------- |
| **Purpose**       | Implement domain ports, configure Spring, handle exceptions                 |
| **Adapters**      | Database (PostgreSQL), Security (API Key), OpenAPI (Swagger)                |
| **Migrations**    | Liquibase (YAML-based, version controlled)                                  |
| **Configuration** | GlobalExceptionHandler, SecurityConfig, OpenApiConfig, ProductServiceConfig |
| **Key Classes**   | ProductPersistenceAdapter, ProductMapper, ProductEntity                     |
| **Testing**       | H2 in-memory database, 17 integration tests (9 exception + 8 auth)          |

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
├── src/main/resources/
│   ├── db/
│   │   └── changelog/
│   │       ├── db.changelog-master.yaml      (Liquibase main)
│   │       └── changes/v1.0.0/               (Migrations)
│   └── logback-spring.xml                    (Logging config)
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

### 6. Liquibase Migration Management

**Automatic schema versioning:**

```yaml
db/
└── changelog/
├── db.changelog-master.yaml
└── changes/
└── v1.0.0/
├── 01-create-products-table.yaml
└── 02-create-index.yaml
```

**How it works:**

1. Application startup triggers Liquibase
2. Checks DATABASECHANGELOG table for executed migrations
3. Runs new migrations in order
4. Creates/updates DATABASECHANGELOCK for concurrency control

**Benefits:**

- ✅ Database schema version control (like Git for DB)
- ✅ Automatic migrations on deployment
- ✅ Rollback support (changeset reversions)
- ✅ Team collaboration (no manual SQL scripts)
- ✅ Audit trail (who changed what and when)

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

## Getting Started

### Run Infrastructure Tests

```bash
# Tests use H2 in-memory database
mvn test -pl hexagonal-infrastructure

# Run specific test
mvn test -pl hexagonal-infrastructure -Dtest=GlobalExceptionHandlerTest
mvn test -pl hexagonal-infrastructure -Dtest=ApiKeyAuthFilterTest
```

⚠️ **Note:** To run the full application with PostgreSQL and see adapters in action, see [Boot Module documentation](../hexagonal-boot/README.md).

## Liquibase Database Migrations

**What it does:**

- Automatically runs on application startup (configured in Boot module)
- Version controls database schema changes
- Handles rollbacks and version tracking
- Located in: `src/main/resources/db/changelog/`

**Migration structure:**

```
db/changelog/
├── db.changelog-master.yaml          (Main changelog)
└── changes/
    └── v1.0.0/
        ├── 01-create-products-table.yaml
        └── 02-create-index.yaml
```

**How migrations work:**

1. Liquibase checks `DATABASECHANGELOG` table on startup
2. Runs new migrations in order
3. Records execution in `DATABASECHANGELOG`
4. Creates `DATABASECHANGELOCK` for concurrency control

**To add new migrations:**

1. Create new file: `db/changelog/changes/v1.1.0/03-add-category.yaml`
2. Include it in `db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/v1.1.0/03-add-category.yaml
```

3. Restart application (runs automatically)

**Example migration:**

```yaml
databaseChangeLog:
  - changeSet:
      id: 3
      author: khalled
      changes:
        - addColumn:
            tableName: products
            columns:
              - column:
                  name: category
                  type: varchar(50)
```

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
