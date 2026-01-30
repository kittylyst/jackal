/*
 * CommandWithDispose.java --
 *
 *	Interface for Commands that need to know when they are deleted
 *	from an interpreter.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: CommandWithDispose.java,v 1.2 1999/07/28 03:41:13 mo Exp $
 */

package tcl.lang;

/**
 * This interface is implemented by Commands that need to know when they are deleted from an
 * interpreter. Most commands do not need to know when they are deleted in Java because Java will
 * garbage collect any allocations made by the command. However, sometimes a command may hold onto
 * resources that must be explicitly released. This interface allows those commands to be notified
 * when they are being deleted from the interpreter.
 */
public sealed interface CommandWithDispose extends Command
    permits tcl.lang.Procedure,
        tcl.lang.ImportedCmdData,
        tcl.lang.cmd.InterpSlaveCmd,
        tcl.lang.cmd.InterpAliasCmd,
        tcl.pkg.itcl.Parse.ClassCmd,
        tcl.pkg.itcl.Parse.ClassProtectionCmd,
        tcl.pkg.itcl.Class.HandleClassCmd,
        tcl.pkg.itcl.Methods.ExecMethod,
        tcl.pkg.itcl.Methods.ExecProc,
        tcl.pkg.itcl.Ensemble.HandleEnsemble,
        tcl.pkg.itcl.Objects.HandleInstanceCmd,
        tcl.pkg.itcl.Cmds.FindClassesCmd,
        tcl.pkg.itcl.Cmds.FindObjectsCmd,
        tcl.pkg.itcl.Cmds.DelClassCmd,
        tcl.pkg.itcl.Cmds.DelObjectCmd,
        tcl.pkg.itcl.Cmds.HandleStubCmd,
        tcl.pkg.itcl.Cmds.IsObjectCmd,
        tcl.pkg.itcl.Cmds.IsClassCmd,
        tcl.pkg.java.ReflectObject {
  /** Called when the interpreter is removing the Tcl command */
  public void disposeCmd();
}
