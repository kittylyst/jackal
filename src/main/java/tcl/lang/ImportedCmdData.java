/*
 * ImportedCmdData.java
 *
 *	An ImportedCmdData instance is used as the Command implementation
 *      (the cmd member of the WrappedCommand class).
 *
 * Copyright (c) 1999 Mo DeJong.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: ImportedCmdData.java,v 1.2 2005/09/12 00:00:50 mdejong Exp $
 */

package tcl.lang;

import tcl.lang.exception.TclException;
import tcl.lang.model.Namespace;
import tcl.lang.model.TclObject;

/**
 * Class which is used as the Command implementation inside a WrappedCommand that has been imported
 * into another namespace. The cmd member of a Wrapped command will be set to an instance of this
 * class when a command is imported. From this ImportedCmdData reference, we can find the "real"
 * command from another namespace.
 */
public final class ImportedCmdData implements Command, CommandWithDispose {
  private WrappedCommand realCmd;

  private WrappedCommand self;

  public String toString() {
    return "ImportedCmd for " + getRealCmd();
  }

  /** Called when the command is invoked in the interp. */
  public void cmdProc(
      Interp interp, // The interpreter for setting result
      // etc.
      TclObject[] objv) // The argument list for the command.
      throws TclException {
    Namespace.invokeImportedCmd(interp, this, objv);
  }

  /** Called when the command is deleted from the interp. */
  public void disposeCmd() {
    Namespace.deleteImportedCmd(this);
  }

  /** "Real" command that this imported command refers to. */
  public WrappedCommand getRealCmd() {
    return realCmd;
  }

  public void setRealCmd(WrappedCommand realCmd) {
    this.realCmd = realCmd;
  }

  /**
   * Pointer to this imported WrappedCommand. Needed only when deleting it in order to remove it
   * from the real command's linked list of imported commands that refer to it.
   */
  public WrappedCommand getSelf() {
    return self;
  }

  public void setSelf(WrappedCommand self) {
    this.self = self;
  }
}
