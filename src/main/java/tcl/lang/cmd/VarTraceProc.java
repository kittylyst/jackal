package tcl.lang.cmd;

import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.Util;
import tcl.lang.VarTrace;
import tcl.lang.exception.TclException;
import tcl.lang.exception.TclRuntimeError;

/** The VarTraceProc object holds the information for a specific trace. */
public final class VarTraceProc implements VarTrace {

  // The command holds the Tcl script that will execute. The flags
  // hold the mode flags that define what conditions to fire under.

  String command;
  int flags;
  boolean newStyle;

  /**
   * Constructor for a CmdTraceProc. It simply stores the flags and command used for this trace
   * proc. details on what it does.
   *
   * @param cmd the command string
   * @param newFlags trace flags
   * @param isNewStyle true if new style options are used when invoking the command string
   */
  VarTraceProc(String cmd, int newFlags, boolean isNewStyle) {
    command = cmd;
    flags = newFlags;
    newStyle = isNewStyle;
  }

  /**
   * Evaluate the script associated with this trace.
   *
   * @param interp the current interpreter
   * @param part1 the name of a scalar variable, or the array name
   * @param part2 the index of an array variable, null if the variable is a scaler
   * @param flags the trace flags that caused this invocation
   * @throws TclException
   */
  public void traceProc(Interp interp, String part1, String part2, int flags) throws TclException {
    if (((this.flags & flags) != 0) && ((flags & TCL.INTERP_DESTROYED) == 0)) {
      StringBuffer sbuf = new StringBuffer(command);

      try {
        Util.appendElement(interp, sbuf, part1);
        if (part2 != null) {
          Util.appendElement(interp, sbuf, part2);
        } else {
          Util.appendElement(interp, sbuf, "");
        }

        if (newStyle) {
          if ((flags & TCL.TRACE_READS) != 0) {
            Util.appendElement(interp, sbuf, "read");
          } else if ((flags & TCL.TRACE_WRITES) != 0) {
            Util.appendElement(interp, sbuf, "write");
          } else if ((flags & TCL.TRACE_UNSETS) != 0) {
            Util.appendElement(interp, sbuf, "unset");
          } else if ((flags & TCL.TRACE_ARRAY) != 0) {
            Util.appendElement(interp, sbuf, "array");
          }
        } else {
          if ((flags & TCL.TRACE_READS) != 0) {
            Util.appendElement(interp, sbuf, "r");
          } else if ((flags & TCL.TRACE_WRITES) != 0) {
            Util.appendElement(interp, sbuf, "w");
          } else if ((flags & TCL.TRACE_UNSETS) != 0) {
            Util.appendElement(interp, sbuf, "u");
          } else if ((flags & TCL.TRACE_ARRAY) != 0) {
            Util.appendElement(interp, sbuf, "a");
          }
        }
      } catch (TclException e) {
        throw new TclRuntimeError("unexpected TclException: " + e);
      }

      // Execute the command.

      interp.eval(sbuf.toString(), 0);
    }
  }
}
