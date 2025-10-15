# Projet Spring Boot avec Architecture Hexagonale

Ce projet est un exemple de microservice Spring Boot utilisant l'architecture hexagonale (Ports & Adapters).

## Structure du Projet

```
src/main/java/com/hexagonal/demo/
├── domain/
│   ├── model/           # Entités du domaine
│   └── ports/
│       ├── api/         # Ports primaires (entrée)
│       └── spi/         # Ports secondaires (sortie)
├── infrastructure/
│   └── adapters/
│       ├── input/       # Adaptateurs d'entrée (REST, etc.)
│       └── output/      # Adaptateurs de sortie (Persistence, etc.)
└── HexagonalApplication.java
```

## Gestion des logs

Spring Boot utilise SLF4J comme façade de logging et Logback comme implémentation par défaut.

### Utilisation dans le code

Pour logger dans vos classes :

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExempleService {
    public void faireAction() {
        log.info("Action réalisée");
        log.error("Erreur détectée");
    }
}
```

Si vous n’utilisez pas Lombok :

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExempleService {
    private static final Logger log = LoggerFactory.getLogger(ExempleService.class);
    // ...
}
```

### Configuration

Le fichier `src/main/resources/application.yml` permet de configurer le niveau de log :

```yaml
logging:
  level:
    root: INFO
    com.hexagonal.demo: DEBUG
```

Pour personnaliser le format ou exporter les logs, créez un fichier `logback-spring.xml` dans `src/main/resources`.

### Bonnes pratiques

- Utilisez `log.info` pour les informations métier
- Utilisez `log.debug` pour le debug technique
- Utilisez `log.error` pour les erreurs
- Ne loggez jamais de données sensibles
- Centralisez les logs en production (ELK, Grafana, etc.)

## Technologies Utilisées

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- PostgreSQL (Production)
- H2 (Tests)
- Liquibase
- Lombok
- MapStruct
- OpenAPI/Swagger
- Spring Cloud OpenFeign
- JUnit 5
- Mockito

## Prérequis

- JDK 17
- Maven
- PostgreSQL

## Configuration

### Base de données

#### Production

- URL: jdbc:postgresql://localhost:5432/hexagonal_db
- Username: postgres
- Password: postgres

#### Tests

- H2 in-memory database

### Documentation API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI docs: http://localhost:8080/api-docs

## Exécution

```bash
# Compilation
mvn clean install

# Lancement
mvn spring-boot:run
```

## Tests

```bash
mvn test
```

## Surveillance

Les endpoints Actuator sont disponibles à :

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/info
- http://localhost:8080/actuator/metrics
