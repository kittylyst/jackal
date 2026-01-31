/*
 * JavaNewCmd.java --
 *
 *	Implements the built-in "java::new" command.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All rights reserved.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: JavaNewCmd.java,v 1.6 2006/04/13 07:36:50 mdejong Exp $
 *
 */

package tcl.pkg.java;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.exception.TclException;
import tcl.lang.exception.TclNumArgsException;
import tcl.lang.model.TclList;
import tcl.lang.model.TclObject;

/*
 * This class implements the built-in "java::new" command.
 */

public final class JavaNewCmd implements Command {

  /**
   * Invoked to process the "java::new" Tcl comamnd. See the user documentation for details on what
   * it does.
   *
   * <p>Side effects: A standard Tcl result is stored in the interpreter.
   *
   * @param interp The interpreter for setting the results and which contains the context
   * @param argv the argument list for the command; objv[0[ is the command name itself
   * @throws TclException
   */
  public void cmdProc(Interp interp, TclObject argv[]) throws TclException {
    if (argv.length < 2) {
      throw new TclNumArgsException(interp, 1, argv, "signature ?arg arg ...?");
    }

    // The "java::new" command can take both array signatures and
    // constructor signatures. We want to know what type of signature
    // is given without throwing and catching exceptions. Thus, we
    // call ArraySig.looksLikeArraySig() to determine quickly whether
    // a argv[1] can be interpreted as an array signature or a
    // constructor signature. This is a much less expensive way than
    // calling ArraySig.get() and then calling JavaInvoke.newInstance()
    // if that fails.

    if (ArraySig.looksLikeArraySig(interp, argv[1])) {
      // Create a new Java array object.

      if ((argv.length < 3) || (argv.length > 4)) {
        throw new TclNumArgsException(interp, 2, argv, "sizeList ?valueList?");
      }

      ArraySig sig = ArraySig.get(interp, argv[1]);
      Class arrayType = sig.arrayType;
      int dimensions = sig.dimensions;

      TclObject sizeListObj = argv[2];
      int sizeListLen = TclList.getLength(interp, sizeListObj);

      if (sizeListLen > dimensions) {
        throw new TclException(
            interp,
            "size list \"" + sizeListObj + "\" doesn't match array dimension (" + dimensions + ")");
      }

      TclObject valueListObj = null;
      if (argv.length == 4) {
        valueListObj = argv[3];
      }

      // Initialize arrayObj according to dimensions of both
      // sizeListObj and valueListObj.

      Object obj =
          ArrayObject.initArray(
              interp, sizeListObj, sizeListLen, 0, dimensions, arrayType, valueListObj);

      interp.setResult(ReflectObject.newInstance(interp, arrayType, obj));
    } else {
      // Create a new (scalar) Java object.

      int startIdx = 2;
      int count = argv.length - startIdx;

      interp.setResult(JavaInvoke.newInstance(interp, argv[1], argv, startIdx, count));
    }
  }
} // end JavaNewCmd
