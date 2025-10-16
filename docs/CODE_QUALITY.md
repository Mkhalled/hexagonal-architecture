## Qualité du Code

### Checkstyle

Checkstyle est un outil qui vérifie si le code Java respecte les conventions de codage définies. Notre configuration vérifie :

1. **Conventions de nommage**

   - Classes, méthodes, variables
   - Constantes
   - Packages

2. **Style de code**

   - Indentation
   - Accolades
   - Longueur des lignes
   - Espaces et sauts de ligne

3. **Bonnes pratiques**
   - Imports inutilisés
   - Documentation Javadoc
   - Visibilité des membres
   - Complexité des méthodes

Pour exécuter Checkstyle :

```bash
# Vérification du style
mvn checkstyle:check

# Génération d'un rapport
mvn checkstyle:checkstyle
```

Les rapports sont générés dans : `target/site/checkstyle.html`

### Configuration Personnalisée

#### Checkstyle

La configuration personnalisée se trouve dans `checkstyle.xml` et définit :

- Limite de 50 lignes par méthode
- Maximum 8 paramètres par méthode
- Documentation Javadoc obligatoire pour les méthodes publiques
- Règles de nommage strictes
- Vérification des imports

### Intégration Continue

Ces vérifications sont automatiquement exécutées pendant la phase de build :

```bash
mvn clean verify
```

Cela garantit que tout le code respecte les standards avant d'être commité.
