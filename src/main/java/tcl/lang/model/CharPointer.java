/*
 * CharPointer.java --
 *
 *	Used in the Parser, this class implements the functionality
 * 	of a C character pointer.  CharPointers referencing the same
 *	script share a reference to one array, while maintaining there
 * 	own current index into the array.
 *
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 * RCS: @(#) $Id: CharPointer.java,v 1.5 2005/10/19 23:37:38 mdejong Exp $
 */

package tcl.lang.model;

/**
 * Used in the Parser, this class implements the functionality of a C character pointer.
 * CharPointers referencing the same script share a reference to one array, while maintaining there
 * own current index into the array.
 */
public class CharPointer {

  private char[] array;

  private int index;

  /** Default initialization. */
  //  CharPointer() {
  //    this.array = null;
  //    this.index = -1;
  //  }

  /**
   * Make a "copy" of the argument. This is used when the index of the original CharPointer
   * shouldn't change.
   */
  public CharPointer(CharPointer c) {
    this.setArray(c.getArray());
    this.setIndex(c.getIndex());
  }

  /**
   * Create an array of chars that is one char more than the length of str. This is used to store \0
   * after the last char in the string without causing exceptions.
   */
  public CharPointer(String str) {
    int len = str.length();
    this.setArray(new char[len + 1]);
    str.getChars(0, len, this.getArray(), 0);
    this.getArray()[len] = '\0';
    this.setIndex(0);
  }

  /**
   * Used to map C style '*ptr' into Java.
   *
   * @return character at the current index
   */
  public char charAt() {
    return (getArray()[getIndex()]);
  }

  /**
   * charAt -- Used to map C style 'ptr[x]' into Java.
   *
   * @param x offset from current index of character to return
   * @return character at the current index plus some value.
   */
  public char charAt(int x) {
    return (getArray()[getIndex() + x]);
  }

  /**
   * Since a '\0' char is stored at the end of the script the true length of the string is one less
   * than the length of array.
   *
   * @return The true size of the string.
   */
  public int length() {
    return (getArray().length - 1);
  }

  /**
   * Get the entire string held in this CharPointer's array.
   *
   * @return A String used for debug.
   */
  @Override
  public String toString() {
    return new String(getArray(), 0, getArray().length - 1);
  }

  /** A string of characters. */
  public char[] getArray() {
    return array;
  }

  public void setArray(char[] array) {
    this.array = array;
  }

  /** The current index into the array. */
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
