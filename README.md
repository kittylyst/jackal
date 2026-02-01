# Jackal — Modern Tcl Language Interpreter in Java

Jackal is a Tcl (Tool Command Language) interpreter implemented in Java. It is based on [JTcl](https://jtcl-project.github.io/jtcl/) and the earlier [Jacl](http://tcljava.sf.net) interpreter. Jackal aims for Tcl 8.4–compatible syntax and commands within the constraints of the JVM, and is modernized with Java 17+ features (sealed types, records, modular package structure).

**Project URL:** <http://jackal.github.io/jtcl/>

## Documentation

See the **`docs/`** directory for detailed documentation:

- **[docs/README.md](docs/README.md)** — Project overview
- **[docs/architecture.md](docs/architecture.md)** — Architecture and core components
- **[docs/package-overview.md](docs/package-overview.md)** — Java package structure
- **[docs/building-and-testing.md](docs/building-and-testing.md)** — Build and run instructions

## Quick start

**Requirements:** Java 17 or higher, Maven 3.x

```bash
mvn clean package
java -jar target/jackal-3.0.0-SNAPSHOT.jar
```

## Installing

Jackal requires Java 17 or higher. Download distributions from:

- <https://github.com/jtcl-project/jtcl/releases>

JTcl binary distributions (jtcl-{version}-bin.zip) are compatible. Unzip into a directory of your choice. Unix/Linux/Mac shell script (`jtcl`) and Windows batch file (`jtcl.bat`) are included.

## License

Jackal is licensed under BSD-style licenses by various copyright holders. See the `license.*` files or <http://jtcl-project.github.io/jtcl/licenses.html>.

## Source code

```bash
git clone https://github.com/jtcl-project/jtcl.git
```

## Mailing list and bug reporting

- Mailing list: <https://groups.google.com/d/forum/jtcl-project>
- Bug reporting: <https://github.com/jtcl-project/jtcl/issues>
