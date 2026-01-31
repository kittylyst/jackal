/*
 * TclException.java --
 *
 *	This file defines the TclException class used by Tcl to report
 *	generic script-level errors and exceptions.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: TclException.java,v 1.3 2005/09/11 20:56:58 mdejong Exp $
 *
 */

package tcl.lang.exception;

import tcl.lang.Interp;
import tcl.lang.TCL;

/**
 * TclException is used to interrupt the Tcl script currently being interpreted by the Tcl
 * Interpreter. Usually, a TclException is thrown to indicate a script level error, e.g.:
 *
 * <p>- A syntax error occurred in a script. - A unknown variable is referenced. - A unknown command
 * is executed. - A command is passed incorrected.
 *
 * <p>A TclException can also be thrown by Tcl control structure commands such as "return" and
 * "continue" to change the flow of control in a Tcl script.
 *
 * <p>A TclException is accompanied by two pieces of information: the error message and the
 * completion code. The error message is a string stored in the interpreter result. After a
 * TclException is thrown and caught, the error message can be queried by Interp.getResult().
 *
 * <p>The completion code indicates why the TclException is generated. It is stored in the compCode
 * field of this class.
 */
@SuppressWarnings("serial")
public class TclException extends Exception {

  /** Stores the completion code of a TclException. */
  private int compCode;

  /**
   * An index that indicates where an error occurs inside a Tcl string. This is used to add the
   * offending command into the stack trace.
   *
   * <p>A negative value means the location of the index is unknown.
   *
   * <p>Currently this field is used only by the Jacl interpreter.
   */
  public int errIndex;

  /**
   * Create an TclException with the given error message and completion code and indicate the
   * location of the error in a script.
   */
  protected TclException(Interp interp, String msg, int ccode, int idx) {
    super(msg);
    if (ccode == TCL.OK) {
      throw new TclRuntimeError(
          "The reserved completion code TCL.OK (0) cannot be used " + "in TclException");
    }
    compCode = ccode;
    errIndex = idx;

    if (interp != null && msg != null) {
      interp.setResult(msg);
    }
  }

  /** Create a TclException with the given completion code. */
  public TclException(int ccode) {
    super();
    if (ccode == TCL.OK) {
      throw new TclRuntimeError("The reserved completion code TCL.OK (0) cannot be used");
    }
    compCode = ccode;
    errIndex = -1;
  }

  /** Create a TclException with the given error message. The completion code is set to ERROR. */
  public TclException(Interp interp, String msg) {
    this(interp, msg, TCL.ERROR, -1);
  }

  /** Create a TclException with the given error message and completion code. */
  public TclException(Interp interp, String msg, int ccode) {
    this(interp, msg, ccode, -1);
  }

  public final int getCompletionCode() {
    return compCode;
  }

  public final void setCompletionCode(int ccode) {
    if (ccode == TCL.OK) {
      throw new TclRuntimeError("The reserved completion code TCL.OK (0) cannot be used");
    }
    compCode = ccode;
  }
}
