# Hexagonal Application Module

Use cases and application orchestration layer - **Spring-managed, domain-focused**.

## Quick Overview

| Aspect           | Details                                                    |
| ---------------- | ---------------------------------------------------------- |
| **Purpose**      | Use case orchestration, bridging domain and infrastructure |
| **Dependencies** | Spring (context, AOP), Domain module, test tools           |
| **Annotation**   | `@Service` for Spring management                           |
| **Key Classes**  | `ProductUseCase`                                           |

## Structure

```
application/
├── port/                    # Uses domain ports
│   ├── api/ProductService   (from domain)
│   └── spi/ProductRepository (from domain)
├── usecase/
│   └── ProductUseCase.java  (orchestrates operations)
└── test/
    └── ProductUseCaseTest.java (use case tests)
```

## Key Components

### 1. ProductUseCase (Orchestrator)

```java
@Service
public class ProductUseCase {
    private final ProductService productService;

    // Constructor injection - Spring manages the domain service
    public ProductUseCase(ProductService productService) {
        this.productService = productService;
    }
}
```

### 2. Use Case Pattern

Each public method represents one business operation:

```java
// Single responsibility: one use case per method
public Product executeCreateProduct(Product product) { ... }
public Optional<Product> executeGetProduct(Long id) { ... }
public List<Product> executeGetAllProducts() { ... }
public Product executeUpdateProduct(Product product) { ... }
public void executeDeleteProduct(Long id) { ... }
```

### 3. Dependencies

- Depends **ONLY** on domain (ProductService)
- Orchestrates domain operations
- No database/HTTP knowledge
- No direct Spring knowledge

## Usage

### From Boot/Infrastructure Layer

```java
@Autowired
private ProductUseCase productUseCase;

// Use case execution
Product result = productUseCase.executeCreateProduct(product);
```

### From Tests

```java
ProductService mockService = mock(ProductService.class);
ProductUseCase useCase = new ProductUseCase(mockService);

Product result = useCase.executeCreateProduct(product);
```

## Integration Points

| Consumer           | How It Uses                              |
| ------------------ | ---------------------------------------- |
| **Infrastructure** | Injects use case, calls methods          |
| **Controllers**    | Calls use case methods for operations    |
| **Services**       | Delegates to use case for business logic |

## Design Philosophy

✅ **Why Application layer?**

- Separates use cases from domain
- Single responsibility per method
- Orchestrates domain operations
- Easy to test without infrastructure
- Reusable across interfaces (REST, GraphQL, gRPC)

✅ **Why @Service annotation?**

- Spring manages lifecycle
- Can be autowired
- Declarative as service tier

## What Not to Do

❌ Don't add domain validation logic here
❌ Don't implement persistence (use domain ports)
❌ Don't add HTTP/REST concerns here
❌ Don't create new exceptions (use domain ones)

## Testing

- Mock ProductService from domain
- Test orchestration logic
- Verify correct service method calls
- All tests pass without Spring context

Run tests:

```bash
mvn test -pl hexagonal-application
```

## Dependencies

| Dependency     | Type      | Scope   | Why                         |
| -------------- | --------- | ------- | --------------------------- |
| Domain         | Module    | Compile | Use cases are orchestrators |
| Spring Context | Framework | Compile | `@Service` annotation       |
| JUnit 5        | Test      | Test    | Unit testing                |
| Mockito        | Test      | Test    | Mock domain service         |

---

**Extends:** Domain layer use cases

**Extended by:** Infrastructure adapters, Controllers

**Next module:** [Infrastructure Layer](../hexagonal-infrastructure/README.md)
