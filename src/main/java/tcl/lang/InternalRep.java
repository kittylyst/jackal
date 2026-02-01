/*
 * InternalRep.java
 *
 *	This file contains the abstract class declaration for the
 *	internal representations of TclObjects.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: InternalRep.java,v 1.4 2000/10/29 06:00:42 mdejong Exp $
 *
 */

package tcl.lang;

import tcl.lang.model.*;

/**
 * This is the interface for implementing internal representation of Tcl objects. A class that
 * implements InternalRep should define the following:
 *
 * <p>(1) the two abstract methods specified in this base class: dispose() duplicate()
 *
 * <p>(2) The method toString()
 *
 * <p>(3) class method(s) newInstance() if appropriate
 *
 * <p>(4) class method set<Type>FromAny() if appropriate
 *
 * <p>(5) class method get() if appropriate
 */
public sealed interface InternalRep
    permits TclBoolean,
        TclByteArray,
        TclDict,
        TclDouble,
        TclIndex,
        TclInteger,
        TclLambda,
        TclList,
        TclString,
        UTF8CharPointer,
        tcl.lang.cmd.NamespaceCmd,
        tcl.pkg.itcl.ItclEnsInvoc,
        tcl.pkg.java.ArraySig,
        tcl.pkg.java.ClassRep,
        tcl.pkg.java.FieldSig,
        tcl.pkg.java.FuncSig,
        tcl.pkg.java.PropertySig,
        tcl.pkg.java.ReflectObject {

  /**
   * Free any state associated with the object's internal rep. This method should not be invoked by
   * user code.
   *
   * <p>Leaves the object in an unusable state.
   */
  default void dispose() {}

  /**
   * Make a copy of an object's internal representation. This method should not be invoked by user
   * code.
   *
   * @return A newly allocated instance of the appropriate type.
   */
  default InternalRep duplicate() {
    return this;
  }
}
