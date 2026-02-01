/*
 * TclInterruptedException.java
 *
 * Copyright (c) 2006 Moses DeJong
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: TclInterruptedException.java,v 1.1 2006/04/27 02:16:13 mdejong Exp $
 *
 */

package tcl.lang.exception;

import tcl.lang.Interp;

/**
 * Signals that an interp has been interrupted via the Interp.setInterrupted() API. This exception
 * is used to unwind the Tcl stack and remove pending events from the Tcl event queue.
 */
public final class TclInterruptedException extends RuntimeException {
  Interp interp;

  public TclInterruptedException(Interp interp) {
    this.interp = interp;
  }

  // This method should be invoked after the Tcl
  // stack has been fully unwound to cleanup
  // Interp state, remove any pending events,
  // and dispose of the Interp object.

  public void disposeInterruptedInterp() {
    interp.disposeInterrupted();
  }
}
