package tcl.lang.cmd;

import tcl.lang.Interp;
import tcl.lang.VarTrace;

public final class VwaitTrace implements VarTrace {

  /*
   * TraceCmd.cmdProc continuously watches this variable across calls to
   * doOneEvent(). It returns immediately when done is set to true.
   */

  boolean done = false;

  /*
   * ----------------------------------------------------------------------
   *
   * traceProc --
   *
   * This function gets called when the variable that "vwait" is currently
   * watching is written to.
   *
   * Results: None.
   *
   * Side effects: The done variable is set to true, so that "vwait" will
   * break the waiting loop.
   *
   * ----------------------------------------------------------------------
   */

  public void traceProc(
      Interp interp, // The current interpreter.
      String part1, // A Tcl variable or array name.
      String part2, // Array element name or NULL.
      int flags) // Mode flags: Should only be TCL.TRACE_WRITES.
      {
    done = true;
  }
} // end VwaitTrace
