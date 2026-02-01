/*
 * TclRuntimeError.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package tcl.lang.exception;

/**
 * Signals that a unrecoverable run-time error in the interpreter. Similar to the panic() function
 * in C.
 */
public class TclRuntimeError extends RuntimeException {
  public TclRuntimeError(String s) {
    super(s);
  }

  public TclRuntimeError(String msg, Throwable cause) {
    super(msg, cause);
  }
}
