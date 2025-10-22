# 📱 Application Module

**Thin orchestration layer** that bridges HTTP (Boot) and business logic (Domain).

---

## 🎯 Purpose

The Application module is **intentionally minimal**:

- Simple delegation to domain services
- Spring-managed for easy injection
- Clear contract between Boot and Domain layers

**Analogy:** It's like a **mail clerk** - receives requests, delegates to appropriate workers (domain), returns responses.

---

## 🏗️ Architecture

```
Boot Layer (ProductController)
         ↓
         │ Creates & injects (Spring)
         ↓
Application Layer (ProductUseCase) @Service
         ↓
         │ Delegates all logic
         ↓
Domain Layer (ProductServiceImpl)
         ↓
         (Business logic happens here)
```

---

## 📂 Module Structure

```
hexagonal-application/
├── src/main/java/com/hexagonal/demo/application/
│   └── service/
│       └── ProductUseCase.java          # Main class (15 lines!)
├── src/test/java/com/hexagonal/demo/application/
│   └── service/
│       └── ProductUseCaseTest.java      # 5 tests
├── pom.xml                              # NOW includes spring-context

└── README.md
```

---

## 🔍 Core Component

### ProductUseCase.java

```java
@Service
@RequiredArgsConstructor
public class ProductUseCase implements ProductService {

    private final ProductService productService;

    @Override
    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return productService.getProduct(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public Product updateProduct(Product product) {
        return productService.updateProduct(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }
}
```

**Key Points:**

- ✅ **@Service annotation** - Spring manages lifecycle & injection
- ✅ **@RequiredArgsConstructor** - Lombok generates constructor
- ✅ **Implements ProductService port** - Same interface as domain impl
- ✅ **Pure delegation** - All methods just forward to domain service

**Why @Service here?**

1. **Simplicity** - Spring handles wiring automatically
2. **Consistency** - Follows Spring conventions
3. **Easy to inject** - Into controllers and other services
4. **Still testable** - Can use `@MockBean` or constructor injection in tests

---

## 🧪 Testing

### ProductUseCaseTest.java — 5 Tests

```java
@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {
    @Mock
    private ProductService productService;

    private ProductUseCase applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ProductUseCase(productService);
    }

    // Test 1: createProduct
    @Test
    void createProduct_ShouldReturnSavedProduct() { ... }

    // Test 2: getAllProducts
    @Test
    void getAllProducts_ShouldReturnListOfProducts() { ... }

    // Test 3: getProduct
    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() { ... }

    // Test 4: updateProduct
    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() { ... }

    // Test 5: deleteProduct
    @Test
    void deleteProduct_ShouldDeleteProduct() { ... }
}
```

**Test Strategy:**

- Still pure unit tests (no Spring context needed!)
- Mock the domain `ProductService`
- Verify each CRUD method delegates correctly
- Can test with or without Spring

**Run Tests:**

```bash
mvn test -pl hexagonal-application
mvn test -pl hexagonal-application -Dtest=ProductUseCaseTest
```

---

## 📦 Dependencies

```xml
<!-- Domain module -->
<dependency>
    <groupId>com.hexagonal</groupId>
    <artifactId>hexagonal-domain</artifactId>
</dependency>

<!-- Spring Framework for @Service -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>

<!-- Testing only -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

**Key:** ✅ **Minimal Spring** (only `spring-context` for `@Service`)

---

## 🔄 Data Flow

```
1. Boot receives HTTP request
   ↓
2. ProductController injects ProductUseCase (Spring finds @Service)
   ↓
3. ProductUseCase.createProduct(product) receives Product
   ↓
4. ProductUseCase delegates: productService.createProduct(product)
   ↓
5. ProductServiceImpl (domain) executes business logic
   ↓
6. ProductServiceImpl calls ProductRepository port
   ↓
7. ProductPersistenceAdapter (infrastructure) saves to DB
   ↓
8. Data bubbles back up to controller
   ↓
9. ProductController returns response
```

---

## 💡 Design Pattern

### **Adapter / Facade Pattern with Spring**

```
┌──────────────────────────────┐
│ ProductUseCase (@Service)    │
│ (Application Facade)         │
├──────────────────────────────┤
│ • Implements: ProductService │
│ • Delegates: all methods     │
│ • Spring-managed: YES        │
│ • Purpose: bridge layers     │
└──────────────────────────────┘
```

**Why Spring here?**

- Application layer is thin enough that Spring overhead is negligible
- Makes wiring simpler and more consistent
- Domain layer still remains pure (no Spring)
- Infrastructure layer also uses Spring
- Follows Spring Boot conventions

---

## 🚀 When to Add Logic Here

The Application module should stay **thin**, but you might add:

### ❌ Should NOT add:

- Business validation (domain responsibility)
- Database queries (infrastructure responsibility)
- HTTP concerns (boot responsibility)

### ✅ Might add:

- **Logging** - Log when use cases run
- **Metrics** - Track operation counts/times
- **Caching** - Cache frequently accessed products
- **Event publishing** - Publish domain events
- **Workflow orchestration** - Complex multi-step use cases

### Example: Future Enhancement

```java
@Service
@RequiredArgsConstructor
public class ProductUseCase implements ProductService {

    private final ProductService productService;
    private final Logger logger;
    private final EventPublisher eventPublisher;

    @Override
    public Product createProduct(Product product) {
        logger.info("Creating product: {}", product.getName());

        Product created = productService.createProduct(product);

        eventPublisher.publish(new ProductCreatedEvent(created));
        logger.info("Product created with ID: {}", created.getId());

        return created;
    }
}
```

---

## 📋 Integration Points

### From Boot (REST Layer)

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productUseCase;  // Injected by Spring

    public ProductController(ProductService productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto) {
        Product product = mapper.toEntity(dto);
        Product created = productUseCase.createProduct(product);
        return ResponseEntity.ok(mapper.toDTO(created));
    }
}
```

Boot layer:

1. Spring finds `@Service ProductUseCase` (implements `ProductService`)
2. Automatically injects it into ProductController
3. Controller calls methods on ProductUseCase
4. ProductUseCase delegates to domain
5. Boot converts responses back to DTOs

---

## ⚙️ How to Use

### In Boot Layer (REST Controller)

```java
@RestController
public class ProductController {
    private final ProductService useCase;

    // Spring injection via constructor
    public ProductController(ProductService useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return useCase.createProduct(product);
    }
}
```

### Testing Application

```java
@Test
void testUseCase() {
    // Can test without Spring!
    ProductService mockDomain = Mockito.mock(ProductService.class);
    ProductUseCase app = new ProductUseCase(mockDomain);

    app.createProduct(product);

    verify(mockDomain).createProduct(product);
}
```

---

## 🎓 Key Takeaways

| Aspect               | Details                                        |
| -------------------- | ---------------------------------------------- |
| **Purpose**          | Thin bridge between Boot and Domain            |
| **Responsibilities** | Delegate CRUD to domain service                |
| **Spring Usage**     | Yes (@Service annotation only)                 |
| **Testing**          | Pure unit tests, no context needed             |
| **Lines of Code**    | ~15 (just delegation)                          |
| **Complexity**       | Intentionally minimal                          |
| **Testability**      | Still excellent (use Mockito)                  |
| **Growth**           | Add logging, metrics, transactions when needed |

---

## 📖 Related Modules

- **[Domain Module](../hexagonal-domain/README.md)** — Where business logic lives
- **[Boot Module](../hexagonal-boot/README.md)** — Where HTTP layer calls this
- **[Infrastructure Module](../hexagonal-infrastructure/README.md)** — Where domain is wired into Spring

---

**Next:** Read [Boot Module README](../hexagonal-boot/README.md) to see how this is integrated
