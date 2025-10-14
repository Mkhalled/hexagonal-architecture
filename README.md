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
