# Building and Testing

## Requirements

- **Java 17** or higher
- **Maven 3.x**

## Build

From the project root:

```bash
mvn clean compile
```

Produces compiled classes under `target/classes`. The main JAR is built with:

```bash
mvn clean package
```

The JAR is created in `target/` and includes the main manifest entry `Main-Class: tcl.tools.Shell` so it can be run with `java -jar target/jackal-3.0.0-SNAPSHOT.jar` (plus any script or arguments).

## Running the shell

After packaging:

```bash
java -jar target/jackal-3.0.0-SNAPSHOT.jar
```

Or run the main class with the classpath:

```bash
mvn exec:java -Dexec.mainClass="tcl.tools.Shell"
```

The Tcl library scripts are loaded from the JAR (from `src/main/resources/tcl/`).

## Tests

Unit and integration tests are in `src/test/java` and use JUnit 5. Tcl-level tests are driven by Java test classes that run `.test` files under `src/test/resources`.

Run all tests:

```bash
mvn test
```

Test results and reports are under `target/surefire-reports`. Coverage (if enabled) is produced by JaCoCo during `mvn verify`.

## Code style

The project uses Spotless with Google Java Format. Format and remove unused imports:

```bash
mvn spotless:apply
```

## Assemblies

The build is configured to produce additional assemblies (e.g. doc, src, bin distributions) via the Maven Assembly Plugin; see `src/main/assembly/` and the `pom.xml` execution for `doc.xml`, `src.xml`, and `bin.xml`.

## Misc scripts

Helper scripts in `misc/` include:

- `mkRelease.sh`, `release_build.sh` — Release builds
- `mkDocRelease.sh`, `mkSrcRelease.sh` — Documentation and source packaging
- `fix-tests.sh` — Test-related fixes

Use them only if they match your workflow; the primary interface is Maven.
