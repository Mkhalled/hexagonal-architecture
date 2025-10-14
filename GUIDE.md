# Guide du Kit de Démarrage - Architecture Hexagonale

## Introduction

Ce document fournit un guide détaillé pour utiliser ce kit de démarrage comme base pour vos microservices Spring Boot avec une architecture hexagonale.

## Qu'est-ce que l'Architecture Hexagonale ?

L'architecture hexagonale (ou "Ports & Adapters") est un modèle architectural qui permet de créer des applications faiblement couplées, facilement testables et maintenables. Elle se compose de trois couches principales :

1. **Domain (Centre)** : Contient la logique métier pure
2. **Ports** : Définit les interfaces pour la communication avec le domaine
3. **Adapters** : Implémente les interfaces pour la communication avec le monde extérieur

## Comment Utiliser ce Kit

### 1. Structure des Packages

```
src/main/java/com/hexagonal/demo/
├── domain/
│   ├── model/           # Entités métier
│   │   └── Product.java
│   ├── ports/
│   │   ├── api/        # Ports d'entrée (Use cases)
│   │   │   └── ProductService.java
│   │   └── spi/        # Ports de sortie
│   │       └── ProductRepository.java
│   └── service/        # Implémentation des use cases
│       └── ProductServiceImpl.java
├── infrastructure/
│   ├── config/         # Configurations
│   │   ├── OpenApiConfig.java
│   │   └── security/
│   │       ├── ApiKeyProperties.java
│   │       ├── ApiKeyAuthFilter.java
│   │       └── SecurityConfig.java
│   └── adapters/
│       ├── input/      # Adaptateurs d'entrée
│       │   └── rest/
│       │       └── ProductController.java
│       └── output/     # Adaptateurs de sortie
│           └── persistence/
│               ├── entity/
│               │   └── ProductEntity.java
│               ├── mapper/
│               │   └── ProductMapper.java
│               └── ProductPersistenceAdapter.java
└── HexagonalApplication.java
```

### 2. Étapes pour Ajouter une Nouvelle Fonctionnalité

#### 2.1. Créer le Modèle de Domaine

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YourDomainModel {
    private Long id;
    // Ajoutez vos attributs métier ici
}
```

#### 2.2. Définir les Ports

Port Primaire (API) :

```java
public interface YourService {
    YourDomainModel create(YourDomainModel model);
    Optional<YourDomainModel> getById(Long id);
    // Autres méthodes métier
}
```

Port Secondaire (SPI) :

```java
public interface YourRepository {
    YourDomainModel save(YourDomainModel model);
    Optional<YourDomainModel> findById(Long id);
    // Autres méthodes de persistence
}
```

#### 2.3. Implémenter le Service

```java
@Service
@RequiredArgsConstructor
public class YourServiceImpl implements YourService {
    private final YourRepository repository;

    @Override
    public YourDomainModel create(YourDomainModel model) {
        // Logique métier ici
        return repository.save(model);
    }
}
```

#### 2.4. Créer l'Entité JPA

```java
@Entity
@Table(name = "your_table")
@Data
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Mappez vos colonnes ici
}
```

#### 2.5. Créer le Mapper

```java
@Mapper(componentModel = "spring")
public interface YourMapper {
    YourEntity toEntity(YourDomainModel model);
    YourDomainModel toDomain(YourEntity entity);
}
```

#### 2.6. Implémenter l'Adaptateur de Persistence

```java
@Component
@RequiredArgsConstructor
public class YourPersistenceAdapter implements YourRepository {
    private final JpaRepository<YourEntity, Long> jpaRepository;
    private final YourMapper mapper;

    @Override
    public YourDomainModel save(YourDomainModel model) {
        var entity = mapper.toEntity(model);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
```

#### 2.7. Créer le Contrôleur REST

```java
@RestController
@RequestMapping("/api/your-resource")
@RequiredArgsConstructor
@Tag(name = "Your Resource")
public class YourController {
    private final YourService service;

    @PostMapping
    public ResponseEntity<YourDomainModel> create(@RequestBody YourDomainModel model) {
        return ResponseEntity.ok(service.create(model));
    }
}
```

### 3. Base de Données

#### 3.1. Ajouter une Migration Liquibase

Dans `db.changelog-master.xml` :

```xml
<changeSet id="your-changeset" author="you">
    <createTable tableName="your_table">
        <column name="id" type="bigint" autoIncrement="true">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <!-- Ajoutez vos colonnes ici -->
    </createTable>
</changeSet>
```

### 4. Tests

#### 4.1. Tests Unitaires

```java
@ExtendWith(MockitoExtension.class)
class YourServiceImplTest {
    @Mock
    private YourRepository repository;

    @InjectMocks
    private YourServiceImpl service;

    @Test
    void shouldCreateSuccessfully() {
        // Arrange
        var model = new YourDomainModel();
        when(repository.save(any())).thenReturn(model);

        // Act
        var result = service.create(model);

        // Assert
        assertThat(result).isNotNull();
        verify(repository).save(any());
    }
}
```

### 5. Sécurité

Pour sécuriser vos endpoints, utilisez l'en-tête `X-API-KEY`. La clé API par défaut est configurable via la variable d'environnement `API_KEY`.

### 6. Documentation API

La documentation Swagger est automatiquement générée. Ajoutez des annotations OpenAPI pour enrichir la documentation :

```java
@Operation(summary = "Créer une nouvelle ressource")
@ApiResponse(responseCode = "201", description = "Ressource créée")
public ResponseEntity<YourDomainModel> create(...) {
    // ...
}
```

## Bonnes Pratiques

1. **Validation** : Utilisez les annotations de validation sur vos modèles de domaine
2. **Exceptions** : Créez des exceptions métier personnalisées
3. **Logging** : Utilisez SLF4J pour le logging
4. **Tests** : Visez une couverture de code élevée
5. **Documentation** : Documentez vos API et votre code

## Checklist de Développement

- [ ] Modèle de domaine créé et validé
- [ ] Ports définis (API et SPI)
- [ ] Service implémenté avec la logique métier
- [ ] Entité JPA et mapper créés
- [ ] Adaptateur de persistence implémenté
- [ ] Contrôleur REST avec documentation OpenAPI
- [ ] Migration Liquibase ajoutée
- [ ] Tests unitaires écrits
- [ ] Documentation mise à jour

## Support et Contribution

Pour contribuer :

1. Fork le projet
2. Créez une branche pour votre fonctionnalité
3. Commitez vos changements
4. Soumettez une Pull Request

## Ressources Additionnelles

- [Architecture Hexagonale](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
