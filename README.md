# SGA - README

Ce dépôt contient une petite application Java Swing et des DAO JDBC.

Objectif
- Fournir une simulation simple (UI) et des squelettes de tests JUnit4 pour les DAO.

Prérequis
- JDK installé (Java 8+ recommandé)
- Eclipse (ou autre IDE) pour ouvrir le projet
- JUnit 4 (junit-4.13.2.jar) et hamcrest-core (pour exécution des tests)

Fichiers importants
- Code production : `src/` (ex. `src/sga/...`)
- Simulation UI : `src/sga/view/Main.java` et `src/sga/view/ProfilView.java`
- Tests créés : `src/SGA1/test/sga/dao/` (ex : `BadgeDAOImplTest.java`, squelettes pour les autres DAO)

Lancer la simulation (depuis Eclipse)
1. Importez le projet dans Eclipse (File -> Import -> Existing Projects into Workspace).
2. Dans Eclipse, exécutez la classe `sga.view.Main` (Run As -> Java Application).

Lancer la simulation (depuis la ligne de commande)
1. Compilez le projet depuis Eclipse ou votre build system pour produire les classes (répertoire `bin` ou `target/classes`).
2. Exécutez :

```powershell
java -cp "C:\Users\User\Downloads\EL\eclipse\eclipse\SGA\bin" sga.view.Main
```

Exécuter les tests JUnit4

Option A - Depuis Eclipse
- Assurez-vous que JUnit4 est sur le build path du projet : clic droit sur projet -> Build Path -> Add Libraries -> JUnit -> JUnit 4.
- Ensuite : clic droit sur la classe de test -> Run As -> JUnit Test.

Option B - Depuis la ligne de commande (simple)
1. Téléchargez `junit-4.13.2.jar` et `hamcrest-core-1.3.jar` et placez-les dans un dossier (ex. `lib/`).
2. Compilez les sources Java si nécessaire.
3. Lancez un test via JUnitCore :

```powershell
java -cp "bin;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore SGA1.test.sga.dao.BadgeDAOImplTest
```

Notes importantes sur les tests
- Les tests créés automatiquement incluent :
  - `BadgeDAOImplTest` : quelques assertions unitaires qui n'appellent pas la DB (contrôles null/exceptions).
  - `PersonneDAOImplTest`, `LieuDAOImplTest`, `HistoriqueEvenementDAOImplTest` : squelettes marqués `@Ignore` car ils nécessitent une configuration de la base.
- Si Eclipse indique que `org.junit.Test` ou `org.junit.Assert` ne sont pas accessibles, ajoutez JUnit4 au Build Path comme indiqué ci-dessus.

Activer des tests sans base de données
- Option 1 (mock) : utilisez Mockito pour mocker `DBConnection.getConnection()` et renvoyer une `Connection` factice. Cela permet de tester le code JDBC en simulant `PreparedStatement`/`ResultSet`.
- Option 2 (base en mémoire) : utilisez H2 (jdbc:h2:mem:) et initialisez un schéma de test dans une phase @BeforeClass des tests. Il faudra adapter `DBConnection` ou fournir une fabrique de connexions configurable pour les tests.

Exemple rapide (pistes)
- Pour Mockito : ajouter `mockito-core` sur le classpath et dans un test, mocker la classe utilitaire qui renvoie la connexion.
- Pour H2 : ajouter `com.h2database:h2` et exécuter les scripts SQL nécessaires pour créer les tables et insérer des données de test avant d'exécuter les DAO.

Prochaines étapes suggérées
- Voulez-vous que j'ajoute des tests unitaires supplémentaires avec Mockito pour stubber `DBConnection` ?
- Ou préférez-vous que j'écrive une configuration de tests d'intégration avec H2 et les scripts de création de tables ?

Contact
- Indiquez-moi la méthode souhaitée (mock vs H2) et je génèrerai les tests et la configuration correspondante.
