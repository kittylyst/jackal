package tcl.lang;

import tcl.lang.exception.TclException;
import tcl.lang.model.TclObject;

/**
 * The PrecTraceProc class is used to implement variable traces for the tcl_precision variable to
 * control precision used when converting floating-point values to strings.
 */
public final class PrecTraceProc implements VarTrace {

  // Maximal precision supported by Tcl.

  static final int TCL_MAX_PREC = 17;

  /**
   * traceProc --
   *
   * <p>This function gets called when the tcl_precision variable is accessed in the given
   * interpreter.
   *
   * <p>Results: None.
   *
   * <p>Side effects: If the new value doesn't make sense then this procedure undoes the effect of
   * the variable modification. Otherwise it modifies Util.precision that's used by
   * Util.printDouble().
   *
   * @see VarTrace#traceProc( Interp, String, String, int)
   * @throws If the action is a TCL.TRACES_WRITE and the new value doesn't make sense.
   */
  public void traceProc(Interp interp, String name1, String name2, int flags) throws TclException {
    // If the variable is unset, then recreate the trace and restore
    // the default value of the format string.
    if ((flags & TCL.TRACE_UNSETS) != 0) {
      if (((flags & TCL.TRACE_DESTROYED) != 0) && ((flags & TCL.INTERP_DESTROYED) == 0)) {
        interp.traceVar(
            name1,
            name2,
            new PrecTraceProc(),
            TCL.GLOBAL_ONLY | TCL.TRACE_WRITES | TCL.TRACE_READS | TCL.TRACE_UNSETS);
        // unset doesn't change value of precision
      }
      return;
    }

    // When the variable is read, reset its value from our shared
    // value. This is needed in case the variable was modified in
    // some other interpreter so that this interpreter's value is
    // out of date.

    if ((flags & TCL.TRACE_READS) != 0) {
      interp.setVar(name1, name2, Util.precision, flags & TCL.GLOBAL_ONLY);
      return;
    }

    // The variable is being written. Check the new value and disallow
    // it if it isn't reasonable.
    //
    // Disallow it if this is a safe interpreter (we don't want
    // safe interpreters messing up the precision of other
    // interpreters).
    if (interp.isSafe()) {
      throw new TclException(interp, "can't modify precision from a safe interpreter");
    }
    TclObject tobj = null;
    try {
      tobj = interp.getVar(name1, name2, (flags & TCL.GLOBAL_ONLY));
    } catch (TclException e) {
      // Do nothing when var does not exist.
    }

    String value;

    if (tobj != null) {
      value = tobj.toString();
    } else {
      value = "";
    }

    StrtoulResult r = interp.getStrtoulResult();
    Util.strtoul(value, 0, 10, r);

    if ((r.getValue() <= 0)
        || (r.getValue() > TCL_MAX_PREC)
        || (r.getValue() > 100)
        || (r.getIndex() == 0)
        || (r.getIndex() != value.length())) {
      interp.setVar(name1, name2, Util.precision, TCL.GLOBAL_ONLY);
      throw new TclException(interp, "improper value for precision");
    }

    Util.precision = (int) r.getValue();
  }
}
