/*
 * InterpAliasCmd.java --
 *
 *	Implements the built-in "interp" Tcl command.
 *
 * Copyright (c) 2000 Christian Krone.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: InterpAliasCmd.java,v 1.6 2006/08/03 23:24:02 mdejong Exp $
 *
 */

package tcl.lang.cmd;

import tcl.lang.CommandWithDispose;
import tcl.lang.Interp;
import tcl.lang.Namespace;
import tcl.lang.TCL;
import tcl.lang.TclException;
import tcl.lang.TclList;
import tcl.lang.TclObject;
import tcl.lang.WrappedCommand;

/**
 * This class implements the alias commands, which are created in response to the built-in "interp
 * alias" command in Tcl.
 */
public final class InterpAliasCmd implements CommandWithDispose {

  /** Name of alias command in slave interp. */
  public TclObject name;

  /** Interp in which target command will be invoked. */
  private Interp targetInterp;

  /**
   * Tcl list making up the prefix of the target command to be invoked in
   *
   * <p>the target interpreter. Additional arguments specified when calling the alias in the slave
   * interp will be appended to the prefix before the command is invoked.
   */
  private TclObject prefix;

  /**
   * Source command in slave interpreter, bound to command that invokes the target command in the
   * target interpreter.
   */
  private WrappedCommand slaveCmd;

  /**
   * Entry for the alias hash table in slave.
   *
   * <p>This is used by alias deletion to remove the alias from the slave interpreter alias table.
   */
  private String aliasEntry;

  /**
   * Interp in which the command is defined. This is the interpreter with the aliasTable in Slave.
   */
  private Interp slaveInterp;

  /**
   * This is the procedure that services invocations of aliases in a slave interpreter. One such
   * command exists for each alias. When invoked, this procedure redirects the invocation to the
   * target command in the master interpreter as designated by the Alias record associated with this
   * command.
   *
   * <p>Results: A standard Tcl result.
   *
   * <p>Side effects: Causes forwarding of the invocation; all possible side effects may occur as a
   * result of invoking the command to which the invocation is forwarded.
   */
  public void cmdProc(
      Interp interp, // Current interpreter.
      TclObject[] argv) // Argument list.
      throws TclException // A standard Tcl exception.
      {
    targetInterp.preserve();

    try {
      targetInterp.nestLevel++;

      targetInterp.resetResult();
      targetInterp.allowExceptions();

      // Append the arguments to the command prefix and invoke the command
      // in the target interp's global namespace.

      TclObject[] prefv = TclList.getElements(interp, prefix);
      TclObject cmd = TclList.newInstance();
      cmd.preserve();
      TclList.replace(interp, cmd, 0, 0, prefv, 0, prefv.length - 1);
      TclList.replace(interp, cmd, prefv.length, 0, argv, 1, argv.length - 1);
      TclObject[] cmdv = TclList.getElements(interp, cmd);

      int result = targetInterp.invoke(cmdv, Interp.INVOKE_NO_TRACEBACK);

      cmd.release();
      targetInterp.nestLevel--;

      // Check if we are at the bottom of the stack for the target
      // interpreter.
      // If so, check for special return codes.

      if (targetInterp.nestLevel == 0) {
        if (result == TCL.RETURN) {
          result = targetInterp.updateReturnInfo();
        }
        if (result != TCL.OK && result != TCL.ERROR) {
          try {
            targetInterp.processUnexpectedResult(result);
          } catch (TclException e) {
            result = e.getCompletionCode();
          }
        }
      }

      interp.transferResult(targetInterp, result);
    } finally {
      targetInterp.release();
    }
  }

  /**
   * Is invoked when an alias command is deleted in a slave. Cleans up all storage associated with
   * this alias.
   *
   * <p>Results: None.
   *
   * <p>Side effects: Deletes the alias record and its entry in the alias table for the interpreter.
   */
  public void disposeCmd() {
    if (aliasEntry != null) {
      slaveInterp.getAliasTable().remove(aliasEntry);
    }

    if (slaveCmd != null) {
      targetInterp.getTargetTable().remove(slaveCmd);
    }

    name.release();
    prefix.release();
  }

  /**
   * Helper function to do the work to actually create an alias.
   *
   * <p>Results: A standard Tcl result.
   *
   * <p>Side effects: An alias command is created and entered into the alias table for the slave
   * interpreter.
   *
   * @param interp interpreter for error reporting
   * @param slaveInterp itnerp where alias cmd will live or from which it will be deleted
   * @param masterInterp interp in which target command will be invoked
   * @param name name of alias cmd
   * @param targetName name of target cmd
   * @param objIx offset of first element in objv
   * @param objv additional arguments to store with alias
   * @throws TclException
   */
  static void create(
      Interp interp, // Interp for error reporting.
      Interp slaveInterp, // Interp where alias cmd will live or from
      // which alias will be deleted.
      Interp masterInterp, // Interp in which target command will be
      // invoked.
      TclObject name, // Name of alias cmd.
      TclObject targetName, // Name of target cmd.
      int objIx, // Offset of first element in objv.
      TclObject[] objv) // Additional arguments to store with alias
      throws TclException {
    String nameStr = name.toString();

    /* Don't allow alias over an interpreter's own slave command - see test interp-14.4 */
    WrappedCommand slaveCmd = Namespace.findCommand(slaveInterp, name.toString(), null, 0);
    if (slaveCmd != null && slaveInterp != null && slaveCmd.cmd == masterInterp.slave) {
      slaveInterp.deleteCommandFromToken(slaveCmd);
      throw new TclException(
          interp, "cannot define or rename alias \"" + name + "\": interpreter deleted");
    }
    InterpAliasCmd alias = new InterpAliasCmd();

    alias.name = name;
    name.preserve();

    alias.slaveInterp = slaveInterp;
    alias.targetInterp = masterInterp;

    alias.prefix = TclList.newInstance();
    alias.prefix.preserve();
    TclList.append(interp, alias.prefix, targetName);
    TclList.insert(interp, alias.prefix, 1, objv, objIx, objv.length - 1);

    slaveInterp.createCommand(nameStr, alias);
    alias.slaveCmd = Namespace.findCommand(slaveInterp, nameStr, null, 0);

    try {
      interp.preventAliasLoop(slaveInterp, alias.slaveCmd);
    } catch (TclException e) {
      // Found an alias loop! The last call to Tcl_CreateObjCommand made
      // the alias point to itself. Delete the command and its alias
      // record. Be careful to wipe out its client data first, so the
      // command doesn't try to delete itself.

      slaveInterp.deleteCommandFromToken(alias.slaveCmd);
      throw e;
    }

    // Make an entry in the alias table. If it already exists delete
    // the alias command. Then retry.

    if (slaveInterp.getAliasTable().containsKey(nameStr)) {
      InterpAliasCmd oldAlias = slaveInterp.getAliasTable().get(nameStr);
      slaveInterp.deleteCommandFromToken(oldAlias.slaveCmd);
    }

    alias.aliasEntry = nameStr;
    slaveInterp.getAliasTable().put(nameStr, alias);

    // Create the new command. We must do it after deleting any old command,
    // because the alias may be pointing at a renamed alias, as in:
    //
    // interp alias {} foo {} bar # Create an alias "foo"
    // rename foo zop # Now rename the alias
    // interp alias {} foo {} zop # Now recreate "foo"...

    masterInterp.getTargetTable().put(alias.slaveCmd, slaveInterp);

    interp.setResult(name);
  }

  /**
   * Deletes the given alias from the slave interpreter given.
   *
   * <p>Results: A standard Tcl result.
   *
   * <p>Side effects: Deletes the alias from the slave interpreter.
   *
   * @param interp interpreter for error reporting
   * @param slaveInterp interp where alias command will be deleted
   * @param name name of alias to delete
   * @throws TclException
   */
  static void delete(
      Interp interp, // Interp for error reporting.
      Interp slaveInterp, // Interp where alias cmd will live or from
      // which alias will be deleted.
      TclObject name) // Name of alias to delete.
      throws TclException {
    // If the alias has been renamed in the slave, the master can still use
    // the original name (with which it was created) to find the alias to
    // delete it.

    String string = name.toString();
    if (!slaveInterp.getAliasTable().containsKey(string)) {
      throw new TclException(interp, "alias \"" + string + "\" not found");
    }

    InterpAliasCmd alias = slaveInterp.getAliasTable().get(string);
    slaveInterp.deleteCommandFromToken(alias.slaveCmd);
  }

  /**
   * Sets the interpreter's result object to a Tcl list describing the given alias in the given
   * interpreter: its target command and the additional arguments to prepend to any invocation of
   * the alias.
   *
   * <p>Results: A standard Tcl result.
   *
   * <p>Side effects: None.
   *
   * @param interp interpreter for error reporting
   * @param slaveInterp interp that contains alias command that will be described
   * @param name name of alias to describe
   * @throws TclException
   */
  static void describe(
      Interp interp, // Interp for error reporting.
      Interp slaveInterp, // Interp where alias cmd will live or from
      // which alias will be deleted.
      TclObject name) // Name of alias to delete.
      throws TclException {
    // If the alias has been renamed in the slave, the master can still use
    // the original name (with which it was created) to find the alias to
    // describe it.

    String string = name.toString();
    if (slaveInterp.getAliasTable().containsKey(string)) {
      InterpAliasCmd alias = (InterpAliasCmd) slaveInterp.getAliasTable().get(string);
      interp.setResult(alias.prefix);
    }
  }

  /**
   * ----------------------------------------------------------------------
   *
   * <p>AliasList -> list
   *
   * <p>Computes a list of aliases defined in a slave interpreter.
   *
   * <p>Results: A standard Tcl result.
   *
   * <p>Side effects: None.
   *
   * <p>----------------------------------------------------------------------
   */
  static void list(
      Interp interp, // Interp for error reporting.
      Interp slaveInterp) // Interp whose aliases to compute.
      throws TclException {
    TclObject result = TclList.newInstance();
    for (var entry : slaveInterp.getAliasTable().entrySet()) {
      InterpAliasCmd alias = (InterpAliasCmd) entry.getValue();
      TclList.append(interp, result, alias.name);
    }
    interp.setResult(result);
  }

  /**
   * helper function, that returns the WrappedCommand of the target command (i.e. the command which
   * is called in the master interpreter).
   *
   * <p>Results: The wrapped command.
   *
   * <p>Side effects: None.
   *
   * @param interp interp for error reporting
   * @return the wrapped command
   */
  public WrappedCommand getTargetCmd(Interp interp) // Interp for error
      // reporting.
      throws TclException {
    TclObject objv[] = TclList.getElements(interp, prefix);
    String targetName = objv[0].toString();
    return Namespace.findCommand(targetInterp, targetName, null, 0);
  }

  /**
   * static helper function, that returns the target interpreter of an alias with the given name in
   * the given slave interpreter.
   *
   * @param slaveInterp
   * @param aliasName
   * @return The target interpreter, or null if no alias was found.
   *     <p>Side effects: None.
   */
  static Interp getTargetInterp(Interp slaveInterp, String aliasName) {
    if (!slaveInterp.getAliasTable().containsKey(aliasName)) {
      return null;
    }

    InterpAliasCmd alias = slaveInterp.getAliasTable().get(aliasName);

    return alias.targetInterp;
  }
} // end InterpAliasCmd
