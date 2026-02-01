/*
 * TclVarException.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package tcl.lang.exception;

import tcl.lang.Interp;
import tcl.lang.TCL;

/** This exception is used to report variable errors in Tcl. */
public class TclVarException extends TclException {

  public TclVarException(
      Interp interp, String name1, String name2, String operation, String reason) {
    super(TCL.ERROR);
    if (interp != null) {
      interp.resetResult();
      if (name2 == null) {
        interp.setResult("can't " + operation + " \"" + name1 + "\": " + reason);
      } else {
        interp.setResult("can't " + operation + " \"" + name1 + "(" + name2 + ")\": " + reason);
      }
    }
  }
}
