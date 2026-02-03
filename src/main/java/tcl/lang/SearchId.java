/*
 * SearchId.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: SearchId.java,v 1.3 2006/01/26 19:49:18 mdejong Exp $
 *
 */

package tcl.lang;

import java.util.Iterator;
import java.util.Map;

/**
 * SearchId is used only by the ArrayVar class. When searchstart is called on an Tcl array, a
 * SearchId is created that contains the Enumerated list of all the array keys; a String that
 * uniquely identifies the searchId for the Tcl array, and an index that is used when to generate
 * other unique strings.
 *
 * @param iterator  An Iterator that stores the list of keys for the ArrayVar. This is used in the ArrayCmd class for the
 *  *      * anymore, donesearch, and nextelement functions.
 * @param str   The unique searchId string
 * @param index Unique index used for generating unique searchId strings
 */
public record SearchId(Iterator<Map.Entry<String, Var>> iterator, String str, int index) {
    /**
     * Return the str that is the unique identifier of the SearchId
     */
    public String toString() {
        return str;
    }
}
