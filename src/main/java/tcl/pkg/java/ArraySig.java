package tcl.pkg.java;

import tcl.lang.InternalRep;
import tcl.lang.Interp;
import tcl.lang.exception.TclException;
import tcl.lang.model.TclList;
import tcl.lang.model.TclObject;
import tcl.pkg.java.reflect.PkgInvoker;

/**
 * // The ArraySig class is used internally by the JavaNewCmd // class. ArraySig implements a new
 * Tcl object type that represents an // array signature used for creating Java arrays. Examples or
 * array // signatures are "int[][]", "java.lang.Object[]" or "[[D".
 */
public final class ArraySig implements InternalRep {

  // The Class object for the array (for example int[][][])
  Class arrayType;

  // The number of dimensions specified by the signature. For example,
  // int[][][] has a dimension of 3.

  int dimensions;

  /*
   * ----------------------------------------------------------------------
   *
   * ArraySig --
   *
   * Creates a new ArraySig instance.
   *
   * Side effects: Member fields are initialized.
   *
   * ----------------------------------------------------------------------
   */

  ArraySig(
      Class type, // Initial value for arrayType.
      int n) // Initial value for dimensions.
      {
    arrayType = type;
    dimensions = n;
  }

  /*
   * ----------------------------------------------------------------------
   *
   * duplicate --
   *
   * Make a copy of an object's internal representation.
   *
   * Results: Returns a newly allocated instance of the appropriate type.
   *
   * Side effects: None.
   *
   * ----------------------------------------------------------------------
   */

  @Override
  public InternalRep duplicate() {
    return new ArraySig(arrayType, dimensions);
  }

  /*
   * ----------------------------------------------------------------------
   *
   * looksLikeArraySig --
   *
   * This method quickly determines whether a TclObject can be interpreted as
   * an array signature or a constructor signature.
   *
   * Results: True if the object looks like an array signature, false
   * otherwise.
   *
   * Side effects: None.
   *
   * ----------------------------------------------------------------------
   */

  static boolean looksLikeArraySig(
      Interp interp, // Current interpreter.
      TclObject signature) // TclObject to check.
      throws TclException {
    InternalRep rep = signature.getInternalRep();
    int sigLen;
    String clsName;

    if (rep instanceof FuncSig) {
      // The string rep of FuncSig can never represent an ArraySig,
      // so we know for sure that signature doesn't look like an
      // ArraySig.

      return false;
    }
    if (rep instanceof ArraySig) {
      return true;
    }

    sigLen = TclList.getLength(interp, signature);
    if (sigLen < 1) {
      return false;
    } else if (sigLen == 1) {
      clsName = signature.toString();
    } else {
      clsName = TclList.index(interp, signature, 0).toString();
    }

    if (clsName.endsWith("[]") || clsName.startsWith("[")) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * ----------------------------------------------------------------------
   *
   * get --
   *
   * Returns the ArraySig internal representation of the constructor or method
   * that matches with the signature and the parameters.
   *
   * Results: The ArraySig given by the signature.
   *
   * Side effects: When successful, the internalRep of the signature object is
   * converted to ArraySig.
   *
   * ----------------------------------------------------------------------
   */

  static ArraySig get(
      Interp interp, // Current interpreter. Stores error
      // message
      // if signature doesn't contain an array sig.
      TclObject signature) // The TclObject to convert.
      throws TclException // Standard Tcl exception.
      {
    InternalRep rep = signature.getInternalRep();
    if ((rep instanceof ArraySig)) {
      // The cached internal rep is a valid array signature, return it.

      return (ArraySig) rep;
    }

    trying:
    {
      if (TclList.getLength(interp, signature) != 1) {
        break trying;
      }

      String clsName = signature.toString();
      if (!(clsName.endsWith("[]")) && !(clsName.startsWith("["))) {
        break trying;
      }
      Class arrayType = JavaInvoke.getClassByName(interp, clsName);

      Class componentType = arrayType;
      while (componentType.isArray()) {
        componentType = componentType.getComponentType();
      }

      if (!PkgInvoker.isAccessible(componentType)) {
        JavaInvoke.notAccessibleError(interp, componentType);
      }

      int dimensions = 0;

      if (clsName.charAt(0) == '[') {
        // If the string begins with '[', count the leading '['s.

        while (clsName.charAt(++dimensions) == '[') {}

      } else {
        // If the string is of the form className[][]..., count
        // the trailing "[]"s.

        int end = clsName.length() - 1;
        while ((end > 0) && (clsName.charAt(end - 1) == '[') && (clsName.charAt(end) == ']')) {
          dimensions++;
          end -= 2;
          ;
        }
      }

      ArraySig sigRep = new ArraySig(arrayType, dimensions);

      signature.setInternalRep(sigRep);
      return sigRep;
    }

    throw new TclException(interp, "bad array signature \"" + signature + "\"");
  }
} // end ArraySig
