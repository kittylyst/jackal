/*
 * ExitCmd.java
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: ExitCmd.java,v 1.1.1.1 1998/10/14 21:09:19 cvsadmin Exp $
 *
 */

package tcl.lang.cmd;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclIO;
import tcl.lang.exception.TclException;
import tcl.lang.exception.TclNumArgsException;
import tcl.lang.model.TclInteger;
import tcl.lang.model.TclObject;

/** This class implements the built-in "exit" command in Tcl. */
public final class ExitCmd implements Command {

  /** See Tcl user documentation for details. */
  public void cmdProc(Interp interp, TclObject argv[]) throws TclException {
    int code;

    if (argv.length > 2) {
      throw new TclNumArgsException(interp, 1, argv, "?returnCode?");
    }
    if (argv.length == 2) {
      code = TclInteger.getInt(interp, argv[1]);
    } else {
      code = 0;
    }

    /*
     * Flush any open write channels.  Don't actually close all the
     * channels here, because pipeline channels may cause a hang
     * if the process won't stop.
     */
    TclIO.flushAllOpenChannels(interp);

    System.exit(code);
  }
}
