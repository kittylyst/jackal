package tcl.pkg.itcl;

import java.util.HashMap;
import tcl.lang.*;
import tcl.lang.exception.TclException;

public final class ItclObject implements ItclEventuallyFreed, VarTrace {
  ItclClass classDefn; // most-specific class
  Command accessCmd; // object access command
  WrappedCommand w_accessCmd; // WrappedCommand for accessCmd

  int dataSize; // number of elements in data array
  Var[] data; // all object-specific data members
  HashMap<String, String> constructed; // temp storage used during construction
  // Maps class name String to the empty string.
  HashMap<String, String> destructed; // temp storage used during destruction

  // Maps class name String to the empty string.

  // Invoke via ItclEventuallyFreed interface when
  // refCount for this instance drops to 0

  public void eventuallyFreed() {
    Objects.FreeObject(this);
  }

  // traceProc is invoked to handle variable traces on
  // the "this" instance variable.

  public void traceProc(Interp interp, String part1, String part2, int flags) throws TclException {
    Objects.TraceThisVar(this, interp, part1, part2, flags);
  }
}
