/*
 * PackageNameException.java
 *
 * Copyright (c) 2006 Moses DeJong
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package tcl.lang.exception;

/**
 * This exception is thrown by the TclClassLoader when an attempt to load a class from any package
 * that starts with the java.* or tcl.* prefix is made.
 */
public class PackageNameException extends RuntimeException {
  public String className;

  public PackageNameException(String msg, String className) {
    super(msg);
    this.className = className;
  }
}
