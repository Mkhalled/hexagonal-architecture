# Projet Spring Boot avec Architecture Hexagonale

Ce projet est un exemple de microservice Spring Boot utilisant l'architecture hexagonale (Ports & Adapters).

## Structure du Projet

```
hexagonal-domain/
  └── src/main/java/com/hexagonal/demo/domain/
      ├── model/                  # Entités du domaine
      ├── ports/
      │   ├── api/               # Ports primaires (entrée)
      │   └── spi/               # Ports secondaires (sortie)
      ├── service/               # Services métier
      └── exception/            # Exceptions métier

hexagonal-application/
  └── src/main/java/com/hexagonal/demo/application/
      ├── service/               # Services d'application
      └── exception/            # Exceptions d'application

hexagonal-infrastructure/
  └── src/main/java/com/hexagonal/demo/infrastructure/
      ├── adapters/
      │   ├── input/rest/        # Contrôleurs REST
      │   └── output/persistence # Accès aux données (JPA, entités, mappers, repository)
      ├── config/
      │   ├── error/             # Gestion des erreurs
      │   └── security/          # Sécurité (API Key, filtre, config)
      └── utils/                 # Utilitaires

hexagonal-boot/
  └── src/main/java/com/hexagonal/demo/HexagonalApplication.java # Classe principale Spring Boot
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

## Fichiers de configuration

**application.yml** :
Fichier principal de configuration Spring Boot. Il regroupe les paramètres de l’application : base de données, serveur, sécurité, logging, gestion des profils, etc. Il se trouve généralement dans resources.

**bootstrap.yml** :
Fichier utilisé pour la configuration initiale du contexte Spring Cloud (activation du cloud config, nom d’application, etc.). Il est chargé avant application.yml si présent. Dans ce projet, il sert à désactiver Spring Cloud Config et à définir le profil actif.

**application-<profile>.yml** :
Fichiers de configuration spécifiques à un profil (ex : application-test.yml, application-prod.yml). Permettent de surcharger la configuration selon l’environnement (test, dev, prod). Le profil actif est défini dans application.yml ou bootstrap.yml.

a**pplication.properties** :
Alternative à application.yml pour une configuration au format propriétés. Non utilisé ici, mais reconnu par Spring Boot.

```
hexagonal-boot/src/main/resources/
├── application.yml           # Configuration principale
├── bootstrap.yml            # Configuration initiale (cloud, profil)
├── application-test.yml     # Surcharge pour le profil 'test' (optionnel)
└── logback-spring.xml       # Configuration avancée des logs (optionnel)

```

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

## Gestion des Erreurs

Le projet implémente une gestion centralisée des erreurs via le `GlobalExceptionHandler`.

### Types d'Erreurs

#### Erreurs Métier

- **ResourceNotFoundException** : Ressource non trouvée (HTTP 404)
- **BusinessValidationException** : Erreur de validation métier (HTTP 400)
- **BusinessException** : Erreur métier générique (HTTP 500)

#### Erreurs Techniques

- **MethodArgumentNotValidException** : Erreur de validation des paramètres (HTTP 400)
- **AccessDeniedException** : Accès non autorisé (HTTP 403)
- **Exception** : Erreurs non gérées (HTTP 500)

Toutes les erreurs sont retournées dans un format standardisé `ApiError` :

```json
{
  "code": "ERROR_CODE",
  "message": "Description de l'erreur",
  "status": 400,
  "path": "/api/resource",
  "timestamp": "2025-10-15T10:30:00"
}
```

### Journalisation des Erreurs

Chaque erreur est automatiquement journalisée avec un niveau approprié :

- Erreurs de validation : WARN
- Erreurs d'accès : ERROR
- Erreurs système : ERROR

### Exemple d'Utilisation

Pour lever une erreur métier :

```java
throw new BusinessValidationException("INVALID_INPUT", "Les données fournies sont invalides");
```

## Surveillance

Les endpoints Actuator sont disponibles à :

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/info
- http://localhost:8080/actuator/metrics
