/*
 * ApplyCmd.java --
 *
 * Copyright (C) 2010 Neil Madden &lt;nem@cs.nott.ac.uk&gt.
 *
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 * RCS: @(#) $Id$
 */

package tcl.lang.cmd;

import tcl.lang.*;
import tcl.lang.exception.TclException;
import tcl.lang.exception.TclNumArgsException;
import tcl.lang.model.TclLambda;
import tcl.lang.model.TclObject;

/**
 * Implementation of the [apply] command.
 *
 * @author Neil Madden &lt;nem@cs.nott.ac.uk&gt;
 * @version $Revision$
 */
public final class ApplyCmd implements Command {

    /**
     *
     * @param interp The interpreter for setting the results and which contains the context
     * @param objv the argument list for the command; objv[0] is the command name itself, objv[1] is the target
     * @throws TclException
     */
  public void cmdProc(Interp interp, TclObject[] objv) throws TclException {
    if (objv.length < 2) {
      throw new TclNumArgsException(interp, 1, objv, "lambdaExpr ?arg ...?");
    }

    TclLambda.apply(interp, objv[1], objv);
  }
}
