/*
 * TclNumArgsException.java
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
import tcl.lang.model.TclIndex;
import tcl.lang.model.TclObject;

/** This exception is used to report wrong number of arguments in Tcl scripts. */
public class TclNumArgsException extends TclException {

  public TclNumArgsException(Interp interp, int argc, TclObject argv[], String message)
      throws TclException {
    super(TCL.ERROR);

    if (interp != null) {
      StringBuffer buff = new StringBuffer(50);
      buff.append("wrong # args: should be \"");

      for (int i = 0; i < argc; i++) {
        if (argv[i].getInternalRep() instanceof TclIndex) {
          buff.append(argv[i].getInternalRep().toString());
        } else {
          buff.append(argv[i].toString());
        }
        if (i < (argc - 1)) {
          buff.append(" ");
        }
      }
      if ((message != null) && (message.length() != 0)) {
        buff.append(" " + message);
      }
      buff.append("\"");
      interp.setResult(buff.toString());
    }
  }
}
