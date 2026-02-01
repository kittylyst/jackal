# Architecture

## Overview

Jackal is a Tcl interpreter implemented in Java. Execution is centered on an **interpreter** (`Interp`) that owns namespaces, variables, commands, channels, and optional extensions. Scripts are parsed and executed in terms of **commands** and **TclObject** values.

## Module

The codebase is a **named Java module** (`jackal`), defined in `src/main/java/module-info.java`. The module:

- Enables sealed class hierarchies that span packages (e.g. `TclEvent`, `Command`).
- Exports the public API packages.
- Requires `java.base`, `java.desktop` (Java Beans / Tcl Blend), and `java.scripting` (JSR 223).

Exported packages include:

- **Core:** `tcl.lang`, `tcl.lang.model`, `tcl.lang.exception`, `tcl.lang.regex`
- **I/O and execution:** `tcl.lang.channel`, `tcl.lang.cmd`, `tcl.lang.process`
- **Embedding:** `tcl.lang.embed.jsr223`
- **Tools:** `tcl.tools` (Shell, AppShell)
- **Extensions:** `tcl.pkg.java`, `tcl.pkg.itcl`, `tcl.pkg.tjc`, `tcl.pkg.fleet`

## Core components

- **Interp** — Central interpreter: namespace stack, variable resolution, command dispatch, channel table, and association data. One `Interp` instance per interpreter.
- **Namespace** (`tcl.lang.model`) — Scopes for commands and variables (e.g. global `::`, user namespaces). Hierarchical naming with `::`.
- **Var** — Variable representation (scalar/array, traces, link/upvar). Lives in call frames or namespace variable tables.
- **CallFrame** — Procedure call frame: local variables, compiled locals, and linkage to caller/namespace.
- **Command** — Sealed interface for all invocable commands (built-in procs, aliases, extensions). Dispatch is by name lookup in the current namespace.
- **TclObject** (`tcl.lang.model`) — Dual-ported Tcl value (string and internal representation, e.g. list, int, dict). Reference-counted where used across boundaries. Object types (`TclList`, `TclDict`, `TclString`, etc.) live in `tcl.lang.model`.

## Execution model

1. Source or script is presented to the interpreter.
2. Parser produces a command tree; each command is a list of words (TclObject).
3. First word is the command name; it is resolved in the current namespace (and resolvers).
4. The resolved `Command` implementation is invoked with `Interp` and the word array.
5. Commands read/write variables via `Var` APIs, call `interp.setResult()` (or equivalent), and can create further call frames for procedures.

## Extensions

Functionality is split between:

- **Core** (`tcl.lang`) — Interp, variables, call frames, parser, expression evaluation, and command infrastructure.
- **Model** (`tcl.lang.model`) — TclObject and internal representations (list, dict, string, integer, double, etc.), plus Namespace.
- **Exception** (`tcl.lang.exception`) — TclException and subclasses (TclNumArgsException, TclVarException, TclPosixException, etc.).
- **Regex** (`tcl.lang.regex`) — Regular expression engine (Regex, TclRegexp).
- **Built-in commands** (`tcl.lang.cmd`) — Standard Tcl commands (e.g. `set`, `proc`, `if`, `while`, `list`, `dict`, `namespace`).
- **Packages** (`tcl.pkg.*`) — Loadable extensions: Java integration, [incr Tcl], TJC, Fleet, etc.
- **Tools** (`tcl.tools`) — Shell (REPL), AppShell, NonInteractiveShell.

Extensions register new commands (and optionally package/namespace hooks) with an existing `Interp`.

## Resources

- **Tcl library** — `src/main/resources/tcl/` holds the Tcl library scripts (init, package ifneeded, encoding data, etc.) packaged into the JAR.
- **JSR 223** — Script engine binding is in `tcl.lang.embed.jsr223`; the engine factory is registered in `META-INF` so the JVM can discover the Tcl engine by name.
