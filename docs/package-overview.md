# Package Overview

## Core (`tcl.lang`)

Interpreter core: `Interp`, `Namespace`, `Var`, `CallFrame`, `TclObject`, and supporting types. Defines the `Command` interface (sealed) and command dispatch, variable resolution, expression evaluation (`Expression`), and object types (list, dict, string, integer, double, etc.). Also contains `Shell` (REPL) and process/pipe support.

## Channels (`tcl.lang.channel`)

I/O abstraction: `Channel` and implementations (file, socket, stdio, etc.) used by `puts`, `gets`, `read`, `fconfigure`, and the Tcl I/O layer.

## Built-in commands (`tcl.lang.cmd`)

Standard Tcl commands implemented as classes that implement `Command`, e.g.:

- Control: `IfCmd`, `WhileCmd`, `ForCmd`, `SwitchCmd`, etc.
- Procedures: `ProcCmd`, `ApplyCmd`, `UplevelCmd`, `UpvarCmd`
- Variables: `SetCmd`, `UnsetCmd`, `ArrayCmd`, `TraceCmd`
- Namespaces: `NamespaceCmd`
- String/list/dict: `StringCmd`, `ListCmd`, `DictCmd`, `LsearchCmd`, etc.
- I/O and system: `FileCmd`, `IoCmd`, `ExecCmd`, `PidCmd`, `EncodingCmd`
- Interpreter: `InterpCmd`, `InterpSlaveCmd`, `InterpAliasCmd`
- And many others

Each command class is registered by name in the global (or appropriate) namespace at startup.

## Process (`tcl.lang.process`)

Process creation and pipeline support used by `exec` and related commands.

## JSR 223 embedding (`tcl.lang.embed.jsr223`)

Script engine implementation so Tcl can be used via `javax.script` (e.g. `ScriptEngineManager.getEngineByName("tcl")`).

## Java package (`tcl.pkg.java`)

Tcl Blend–style Java integration: `java::*` commands (e.g. `java::new`, `java::call`, `java::import`, `java::bind`). Exposes Java objects as Tcl values (`ReflectObject`), method/constructor invocation, and event binding. Uses `tcl.pkg.java.reflect` for reflection-based invocation.

## [incr Tcl] package (`tcl.pkg.itcl`)

Object-oriented extension: classes, objects, methods, inheritance, and namespaces. Implemented in `tcl.pkg.itcl` (e.g. `Class`, `Objects`, `Methods`, `Cmds`, `BiCmds`, `Util`). Registers `itcl::*` commands and integrates with the core namespace and variable system.

## TJC package (`tcl.pkg.tjc`)

Tcl-to-Java compilation: compiles Tcl procedures to JVM bytecode for faster execution. Provides `tjc::*` commands and uses the Fleet package for execution.

## Fleet package (`tcl.pkg.fleet`)

Multi-threaded execution support: worker members and message queue used by TJC (and potentially other callers) to run compiled or scripted code on separate threads.
