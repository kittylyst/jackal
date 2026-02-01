/*
 * Extension.java --
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: Extension.java,v 1.5 2006/04/13 07:36:50 mdejong Exp $
 *
 */

package tcl.lang;

import tcl.lang.exception.TclException;

/**
 * Base class for all Tcl Extensions. A Tcl Extension defines a set of commands that can be loaded
 * into an Interp as a single unit.
 *
 * <p>When a Tcl Extension is loaded into an Interp, either statically (using the "new" operator
 * inside Java code) or dynamically (using the java::load command in Tcl scripts), it usually
 * creates a set of commands inside the interpreter. Occasionally, loading an Extension may lead to
 * additional side effects. For example, a communications Extension may open network connections
 * when it's loaded. Please refer to the documentation of the specific Extension for details.
 */
public abstract class Extension {

  /**
   * Default constructor. Does nothing. The purpose of this constructor is to make sure instances of
   * this Extension can be loaded dynamically using the "java::load" command, which calls
   * Class.newInstance().
   */
  public Extension() {}

  /**
   * Initialize the Extension to run in a normal (unsafe) interpreter. This usually means creating
   * all the commands provided by this class. A particular implementation can arrange the commands
   * to be loaded on-demand using the loadOnDemand() function.
   *
   * @param interp current interpreter.
   */
  public abstract void init(Interp interp) throws TclException;

  /**
   * Initialize the Extension to run in a safe interpreter. This method should be written carefully,
   * so that it initializes the safe interpreter only with partial functionality provided by the
   * Extension that is safe for use by untrusted code.
   *
   * <p>The default implementation always throws a TclException, so that a subclass of Extension
   * cannot be loaded into a safe interpreter unless it has overridden the safeInit() method.
   *
   * @param safeInterp the safe interpreter in which the Extension should be initialized.
   */
  public void safeInit(Interp safeInterp) throws TclException {
    throw new TclException(
        safeInterp,
        "Extension \"" + getClass().toString() + "\" cannot be loaded into a safe interpreter");
  }

  /**
   * Create a stub command which autoloads the real command the first time the stub command is
   * invoked. Register the stub command in the interpreter.
   *
   * @param interp current interp.
   * @param cmdName name of the command, e.g., "after".
   * @param clsName name of the Java class that implements this command, e.g. "tcl.lang.AfterCmd"
   */
  public static final void loadOnDemand(Interp interp, String cmdName, String clsName) {
    interp.createCommand(cmdName, new AutoloadStub(clsName));
  }
}
