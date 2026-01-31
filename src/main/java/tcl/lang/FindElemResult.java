/*
 * FindElemResult.java --
 *
 *	Result returned by Util.findElement().
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: FindElemResult.java,v 1.4 2005/11/22 22:10:02 mdejong Exp $
 *
 */

package tcl.lang;

// Result returned by Util.findElement().

public final class FindElemResult {

  // The start index of the element in the original string -- the index of the
  // first character in the element.

  private int elemStart;

  // The end index of the element in the original string -- the index of the
  // character immediately behind the element.

  private int elemEnd;

  // The number of characters parsed from the original string, this can be
  // different than the length of the elem string when two characters
  // are collapsed into one in the case of a backslash.

  private int size;

  // The element itself.
  private String elem;

  /**
   * Update a FindElemResult, this method is used only in the Util.findElement() API.
   *
   * @param start Initial value for elemStart.
   * @param end Initial value for elemEnd.
   * @param elem Initial value for elem.
   * @param size Initial value for size.
   */
  void update(int start, int end, String elem, int size) {
    this.setElemStart(start);
    this.setElemEnd(end);
    this.setElem(elem);
    this.setSize(size);
  }

  public int getElemStart() {
    return elemStart;
  }

  public void setElemStart(int elemStart) {
    this.elemStart = elemStart;
  }

  public int getElemEnd() {
    return elemEnd;
  }

  public void setElemEnd(int elemEnd) {
    this.elemEnd = elemEnd;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getElem() {
    return elem;
  }

  public void setElem(String elem) {
    this.elem = elem;
  }
}
