# Hexagonal Boot Module

Application entry point and REST API exposure - **Spring Boot application container**.

## Quick Overview

| Aspect              | Details                                                   |
| ------------------- | --------------------------------------------------------- |
| **Purpose**         | Application bootstrap, REST endpoints, HTTP configuration |
| **Main Class**      | `HexagonalApplication`                                    |
| **REST Controller** | `ProductController`                                       |
| **Profiles**        | `dev`, `test`                                             |
| **Port**            | 8080 (default)                                            |

## Structure

```
boot/
├── HexagonalApplication.java        (Spring Boot main)
├── config/
│   └── ApplicationProperties.java   (Configuration properties)
├── controller/
│   └── ProductController.java       (REST endpoints)
├── resources/
│   ├── application.yml              (default)
│   ├── application-dev.yml          (development - PostgreSQL)
│   ├── application-test.yml         (testing - H2)
│   └── db/changelog/                (Liquibase migrations)
└── test/
    └── ProductControllerTest.java   (7 integration tests)
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

#### Development (PostgreSQL)

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hexagonal_db
    username: postgres
    password: postgres
  liquibase:
    enabled: true
```

#### Testing (H2 In-Memory)

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  liquibase:
    enabled: false
```

## API Endpoints

| Method | Endpoint             | Purpose        | Returns        |
| ------ | -------------------- | -------------- | -------------- |
| POST   | `/api/products`      | Create product | 201 Created    |
| GET    | `/api/products/{id}` | Get product    | 200 OK or 404  |
| GET    | `/api/products`      | List all       | 200 OK         |
| PUT    | `/api/products/{id}` | Update product | 200 OK or 404  |
| DELETE | `/api/products/{id}` | Delete product | 204 No Content |

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

## Running the Application

### Development Mode

```bash
# Run with PostgreSQL
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Testing Mode

```bash
# Run tests with H2
mvn test -pl hexagonal-boot
```

### Docker (Optional)

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar hexagonal-boot/target/hexagonal-boot-1.0.0.jar
```

## Test Coverage

7 integration tests using `@SpringBootTest`:

```
✓ Create product (validation, persistence)
✓ Get product (found, not found)
✓ List all products
✓ Update product
✓ Delete product
✓ Exception handling (400, 404, 500)
✓ Error response format
```

Run tests:

```bash
mvn test -pl hexagonal-boot
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
