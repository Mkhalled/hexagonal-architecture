# Hexagonal Domain Module

Core business logic layer - **pure Java, no frameworks**.

## Quick Overview

| Aspect | Details |
|--------|---------|
| **Purpose** | Business logic, rules, and domain exceptions |
| **Dependencies** | Lombok, JUnit5, Mockito (test only) |
| **Spring** | ❌ None - completely framework-agnostic |
| **Key Classes** | `Product`, `ProductService`, `ProductRepository` |

## Structure

```
domain/
├── exception/          # Domain-specific exceptions with error codes
│   ├── BusinessException.java (parent with error code)
│   ├── BusinessValidationException
│   └── ResourceNotFoundException
├── model/              # Domain entities
│   └── Product.java (name, price, quantity, description)
├── ports/              # Contracts (interfaces)
│   ├── api/ProductService (what domain offers)
│   └── spi/ProductRepository (what domain needs)
└── service/
    └── ProductServiceImpl (CRUD + validation logic)
```

## Key Components

### 1. Product Entity
```java
Product.builder()
    .name("Laptop")
    .price(BigDecimal.valueOf(1299.99))
    .quantity(5)
    .build();
```

### 2. Ports (Contracts)
- **API Port** `ProductService`: CRUD interface domain exposes
- **SPI Port** `ProductRepository`: Database interface domain depends on

### 3. Business Rules
```
✓ Product name cannot be empty
✓ Price must be >= 0
✓ Quantity must be >= 0
✓ Product must exist before update/delete
```

### 4. Exceptions
- `BusinessValidationException` - validation failures
- `ResourceNotFoundException` - entity not found
- Both include error codes for API responses

## Usage

### Creating a Product
```java
ProductService service = new ProductServiceImpl(repository);
Product product = service.createProduct(
    Product.builder()
        .name("Mouse")
        .price(BigDecimal.valueOf(29.99))
        .quantity(50)
        .build()
);
```

### Error Handling
```java
try {
    service.getProduct(999L);
} catch (ResourceNotFoundException e) {
    System.out.println(e.getCode()); // "RESOURCE_NOT_FOUND"
}
```

## Testing

7 unit tests cover all CRUD operations and error scenarios:
```
✓ Create valid/invalid products
✓ Retrieve existing/non-existing products
✓ List all products
✓ Update with validation
✓ Delete with existence check
```

Run tests:
```bash
mvn test -pl hexagonal-domain
```

## Integration Points

| Layer | Interaction |
|-------|-------------|
| **Application** | Uses `ProductService` interface |
| **Infrastructure** | Implements `ProductRepository` interface |
| **Boot** | Injects both via Spring context |

## Design Philosophy

✅ **Why pure Java?**
- Framework-agnostic (reusable anywhere)
- Fast unit tests (no Spring context)
- Business logic stays independent
- Easier to understand and maintain

✅ **Why constructor injection?**
- Enforces non-null repository
- Makes dependencies explicit
- Testable without magic

## What Not to Do

❌ Don't add Spring annotations here
❌ Don't import `org.springframework.*`
❌ Don't handle HTTP/REST concerns here
❌ Don't access database directly (use repository port)

---

**Extend with:** More entities, value objects, domain events, aggregates

**Next module:** [Application Layer](../hexagonal-application/README.md)