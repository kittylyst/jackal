package tcl.pkg.itcl;

import tcl.lang.TclObject;

// Next local var or null for last local.

/*
 * int flags; // Flag bits for the local variable. Same as // the flags for
 * the Var structure above, // although only VAR_SCALAR, VAR_ARRAY, //
 * VAR_LINK, VAR_ARGUMENT, VAR_TEMPORARY, and // VAR_RESOLVED make sense.
 */

// default argument value, null if not
// and argument or no default.

// Name of local variable, can be null.

public record CompiledLocal(
  CompiledLocal next,
  TclObject defValue,
  String name
) {}
