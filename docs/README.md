# Jackal Documentation

High-level documentation for the Jackal project.

## Contents

- **[Architecture](architecture.md)** — Interpreter design, module structure, and main components.
- **[Package overview](package-overview.md)** — Java packages and their roles.
- **[Building and testing](building-and-testing.md)** — Build, test, and run instructions.

## What is Jackal?

Jackal is a modern Tcl (Tool Command Language) interpreter implemented in Java. It is based on [JTcl](https://jtcl-project.github.io/jtcl/) and the earlier [Jacl](http://tcljava.sf.net) interpreter. Jackal aims for Tcl 8.4–compatible syntax and commands within the constraints of the JVM.

The codebase uses a modular package layout: `tcl.lang.model` for value types, `tcl.lang.exception` for exceptions, `tcl.lang.regex` for regular expressions, `tcl.tools` for shells, and core interpreter logic in `tcl.lang`.

**Requirements:** Java 17 or higher.

**Project URL:** <http://jackal.github.io/jtcl/>
