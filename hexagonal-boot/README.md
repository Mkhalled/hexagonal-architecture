# Hexagonal Boot Module

Application entry point and REST API exposure - **Spring Boot application container**.

📚 **This module provides:**

- REST API with 5 CRUD endpoints
- OpenAPI/Swagger documentation
- X-API-Key authentication
- Integration with Domain and Infrastructure layers
- Multiple configuration profiles (dev, prod, test)

## Quick Overview

| Aspect              | Details                                                   |
| ------------------- | --------------------------------------------------------- |
| **Purpose**         | Application bootstrap, REST endpoints, HTTP configuration |
| **Main Class**      | `HexagonalApplication`                                    |
| **REST Controller** | `ProductController` (5 endpoints)                         |
| **Profiles**        | `dev`, `prod`, `test`                                     |
| **Port**            | 8080 (default)                                            |
| **Documentation**   | Swagger UI, OpenAPI JSON                                  |
| **Authentication**  | X-API-Key header required                                 |
| **Tests**           | 7 integration tests with MockMvc                          |

## Structure

```
boot/
├── HexagonalApplication.java        (Spring Boot main)
├── controller/
│   └── ProductController.java       (REST endpoints - 5 CRUD operations)
├── src/main/resources/
│   ├── application.yml              (default configuration)
│   ├── application-dev.yml          (development - PostgreSQL)
│   ├── application-prod.yml         (production - PostgreSQL)
│   └── application-test.yml         (testing - H2 in-memory)
└── src/test/java/
    └── ProductControllerTest.java   (7 integration tests with MockMvc)
```

## Key Components

### 1. Spring Boot Application

```java
@SpringBootApplication
public class HexagonalApplication {
    public static void main(String[] args) {
        SpringApplication.run(HexagonalApplication.class, args);
    }
}
```

### 2. REST Controller (API Entry Points)

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductUseCase productUseCase;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) { ... }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) { ... }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() { ... }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(...) { ... }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { ... }
}
```

### 3. Configuration

## Configuration

### Application Profiles

| Profile | File                   | Purpose     | Database              | Swagger     |
| ------- | ---------------------- | ----------- | --------------------- | ----------- |
| `dev`   | `application-dev.yml`  | Development | PostgreSQL            | ✅ Enabled  |
| `prod`  | `application-prod.yml` | Production  | PostgreSQL (env vars) | ❌ Disabled |
| `test`  | `application-test.yml` | Testing     | H2 in-memory          | ❌ Disabled |

### Default Configuration (application.yml)

```yaml
spring:
  application:
    name: hexagonal-demo
  profiles:
    active: dev # Default profile

server:
  port: 8080
  servlet:
    context-path: /api # Base path for all endpoints

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### Development Configuration (application-dev.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hexagonal_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: none # Liquibase handles schema
    show-sql: true # Log SQL queries
    properties:
      hibernate:
        format_sql: true

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    enabled: true # Run migrations

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method

logging:
  level:
    com.hexagonal.demo: DEBUG
    org.springframework.security: INFO

api:
  security:
    apiKeys:
      - "ma_cle_api_test" # Test API key
```

### Production Configuration (application-prod.yml)

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/hexagonal_db}
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:postgres}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  liquibase:
    enabled: true

springdoc:
  api-docs:
    enabled: false # Hide API docs in prod
  swagger-ui:
    enabled: false

logging:
  level:
    com.hexagonal.demo: INFO
```

### Testing Configuration (application-test.yml)

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
    enabled: false # H2 auto-creates schema
```

## Environment Variables

### Development (Local)

```bash
# PostgreSQL connection
DATABASE_URL=jdbc:postgresql://localhost:5432/hexagonal_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```

### Production (Docker/Cloud)

```bash
# Override in environment
DATABASE_URL=jdbc:postgresql://prod-db:5432/hexagonal_db
DATABASE_USERNAME=${PROD_DB_USER}
DATABASE_PASSWORD=${PROD_DB_PASS}
```

## API Endpoints

All endpoints require `X-API-Key: ma_cle_api_test` header.

### Product Management

| Method     | Endpoint             | Purpose            | Returns        |
| ---------- | -------------------- | ------------------ | -------------- |
| **POST**   | `/api/products`      | Create new product | 201 Created    |
| **GET**    | `/api/products`      | List all products  | 200 OK         |
| **GET**    | `/api/products/{id}` | Get product by ID  | 200 OK / 404   |
| **PUT**    | `/api/products/{id}` | Update product     | 200 OK / 404   |
| **DELETE** | `/api/products/{id}` | Delete product     | 204 No Content |

### Response Examples

**Create (201):**

```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99,
  "quantity": 5
}
```

**Get (404):**

```json
{
  "error": "Product avec l'identifiant 999 n'a pas été trouvé",
  "code": "RESOURCE_NOT_FOUND",
  "timestamp": "2025-10-22T12:30:00Z"
}
```

**Validation Error (400):**

```json
{
  "error": "Validation failed",
  "code": "VALIDATION_ERROR",
  "details": {
    "price": "must not be null"
  }
}
```

### Example Usage

```bash
# Create
curl -X POST http://localhost:8080/api/products \
  -H "X-API-Key: your-key" \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":1299.99,"quantity":5}'

# Get
curl http://localhost:8080/api/products/1 \
  -H "X-API-Key: your-key"

# List
curl http://localhost:8080/api/products \
  -H "X-API-Key: your-key"
```

## Authentication

### X-API-Key Header

All endpoints require: `X-API-Key: your-api-key`

### Exemptions

- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/actuator/**`

## Getting Started

### 1. Prerequisites

- PostgreSQL running (or Docker Compose)
- Java 17+
- Maven 3.9+

```bash
# Start PostgreSQL with Docker Compose
docker-compose up -d
```

### 2. Run Application (Development)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Application starts at: `http://localhost:8080/api`

### 3. Access Documentation

#### Swagger UI (Interactive API Explorer)

```
http://localhost:8080/api/swagger-ui.html
```

**Features:**

- ✅ Try out endpoints directly
- ✅ See request/response examples
- ✅ API Key authentication built-in
- ✅ Real-time API documentation

**Example in Swagger:**

1. Go to `http://localhost:8080/api/swagger-ui.html`
2. Click on "Products" tag to expand
3. Try "POST /api/products" to create
4. Click "Try it out"
5. Authorize with API Key (top right)
6. Send request

#### OpenAPI JSON Schema

```
http://localhost:8080/api/v3/api-docs
```

Machine-readable API specification (for code generation, etc.)

#### ReDoc (Alternative Documentation UI)

```
http://localhost:8080/api/swagger-ui.html?urls.primaryName=ReDoc
```

### 4. Test API

#### Create Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "X-API-Key: ma_cle_api_test" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "quantity": 5
  }'
```

#### Get All Products

```bash
curl -X GET http://localhost:8080/api/products \
  -H "X-API-Key: ma_cle_api_test"
```

#### Get Specific Product

```bash
curl -X GET http://localhost:8080/api/products/1 \
  -H "X-API-Key: ma_cle_api_test"
```

#### Update Product

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "X-API-Key: ma_cle_api_test" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "price": 1199.99,
    "quantity": 3
  }'
```

#### Delete Product

```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "X-API-Key: ma_cle_api_test"
```

### 5. Run Tests

```bash
# Run all Boot tests
mvn test -pl hexagonal-boot

# Run specific test
mvn test -pl hexagonal-boot -Dtest=ProductControllerTest
```

## Test Coverage

7 integration tests using `@SpringBootTest` and `MockMvc`:

| Test                                         | Purpose               | Scenario                           |
| -------------------------------------------- | --------------------- | ---------------------------------- |
| `createProduct_shouldReturnCreated`          | Create endpoint       | Valid product → 201                |
| `getProduct_shouldReturnProduct`             | Get by ID (found)     | Product exists → 200               |
| `getProduct_notFound_shouldReturn404`        | Get by ID (not found) | Product doesn't exist → 404        |
| `getAllProducts_shouldReturnList`            | List endpoint         | Multiple products → 200            |
| `getAllProducts_empty_shouldReturnEmptyList` | List endpoint (empty) | No products → 200 with empty array |
| `updateProduct_shouldReturnOk`               | Update endpoint       | Valid update → 200                 |
| `deleteProduct_shouldReturnNoContent`        | Delete endpoint       | Successful delete → 204            |

### Test Details

**Technology Stack:**

- `@SpringBootTest` - Full Spring context
- `@AutoConfigureMockMvc` - MockMvc for HTTP testing
- `@MockBean` - Mock ProductService
- `@ActiveProfiles("test")` - H2 in-memory database

**Testing Approach:**

```java
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void createProduct_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Laptop\", ...}"))
                .andExpect(status().isCreated());
    }
}
```

### Run Tests

```bash
# All Boot tests
mvn test -pl hexagonal-boot

# Specific test
mvn test -pl hexagonal-boot -Dtest=ProductControllerTest

# With verbose output
mvn test -pl hexagonal-boot -Dtest=ProductControllerTest -X
```

## Dependency Injection

Boot module wires everything:

```java
// Domain layer
ProductService productService = new ProductServiceImpl(repository);

// Application layer
ProductUseCase productUseCase = new ProductUseCase(productService);

// Spring configuration handles all wiring
// Controllers just @Autowire what they need
```

## Configuration Properties

| Property    | Default | Dev        | Test     |
| ----------- | ------- | ---------- | -------- |
| Server Port | 8080    | 8080       | 8080     |
| DB Type     | -       | PostgreSQL | H2       |
| Liquibase   | -       | enabled    | disabled |
| API Docs    | -       | enabled    | disabled |

## Design Philosophy

✅ **Why separate boot module?**

- Entry point only (no business logic)
- Easy to swap Spring Boot for Quarkus/Micronaut
- Multiple boot modules possible (REST, GraphQL, gRPC)
- Clean separation of concerns

✅ **Why dependency injection here?**

- All beans wired in one place
- Easy to test individual layers
- Configuration centralized

✅ **Why profiles?**

- Different configs per environment
- Easy database switching
- Test isolation

## What Not to Do

❌ Don't add business logic here
❌ Don't access repositories directly (use use cases)
❌ Don't validate at controller level (validation in domain)
❌ Don't throw technical exceptions (convert to domain exceptions)

## Monitoring & Health

### Actuator Endpoints

- `/actuator/health` - Application health
- `/actuator/metrics` - Performance metrics

## Extending

### Add New REST Controller

```java
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    // Similar pattern to ProductController
}
```

### Add New Profile

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/hexagonal_db
```

---

**Entry point for:** REST API, Application bootstrap

**Depends on:** Application, Infrastructure, Domain modules

**Global README:** [Architecture Overview](../README.md)
