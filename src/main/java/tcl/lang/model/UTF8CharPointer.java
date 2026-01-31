package tcl.lang.model;

import tcl.lang.InternalRep;
import tcl.lang.cmd.StringCmd;
import tcl.lang.exception.TclRuntimeError;

/**
 * // This class is used to map UTF8 oriented byte indexes used in // the Tcl API for the parser
 * extension into character oriented // index used within Jacl.
 *
 * <p>// String "Foo\u00c7bar" // Chars: 0123 456
 *
 * <p>// Bytes: charToByteIndex byteToCharIndex // [0] = 'f' [0] = 0 [0] = 0 // [1] = '0' [1] = 1
 * [1] = 1 // [2] = 'o' [2] = 2 [2] = 2 // [3] = '?' [3] = 3 [3] = 3 // [4] = '?' [4] = 3 // [5] =
 * 'b' [4] = 5 [5] = 4 // [6] = 'a' [5] = 6 [6] = 5 // [7] = 'r' [6] = 7 [7] = 6
 */
public final class UTF8CharPointer extends CharPointer implements InternalRep {
  int[] charToByteIndex; // Map char index to byte index
  int[] byteToCharIndex; // Map byte index to char index
  byte[] bytes;
  String orig;

  public UTF8CharPointer(String s) {
    super(s);
    orig = s;
    getByteInfo();
  }

  void getByteInfo() {
    int charIndex, byteIndex, bytesThisChar, bytesTotal;

    try {
      // First, loop over the characters to see if each of the characters
      // can be represented as a single UTF8 byte. In this special
      // case there is no need to worry about mapping bytes to charaters
      // or vice versa.

      char c;
      boolean singleBytes = true;

      for (int i = 0; i < getArray().length; i++) {
        c = getArray()[i];
        if (c == '\0') {
          // Ignore encoding issues related to null byte in Java vs
          // UTF8
          bytesThisChar = 1;
        } else {
          bytesThisChar = StringCmd.Utf8Count(c);
        }
        if (bytesThisChar != 1) {
          singleBytes = false;
          break;
        }
      }

      // When each character maps to a single byte, bytes is null

      if (singleBytes) {
        bytes = null;
        return;
      }

      // When multiple byte UTF8 characters are found, convert to
      // a byte array and save mapping info.

      String chars = new String(getArray()); // Get string including trailing
      // null
      bytes = chars.getBytes("UTF8");

      if (chars == null) { // For debugging only
        System.out.println("chars is \"" + chars + "\" len = " + chars.length());
        String bstr = new String(bytes, 0, bytes.length, "UTF8");
        System.out.println("bytes is \"" + bstr + "\" len = " + bytes.length);
      }

      // Count UTF8 bytes for each character, map char to byte index

      charToByteIndex = new int[getArray().length];

      for (charIndex = 0, byteIndex = 0; charIndex < charToByteIndex.length; charIndex++) {
        charToByteIndex[charIndex] = byteIndex;

        c = getArray()[charIndex];
        if (c == '\0') {
          // Ignore encoding issues related to null byte in Java vs
          // UTF8
          bytesThisChar = 1;
        } else {
          bytesThisChar = StringCmd.Utf8Count(c);
        }
        byteIndex += bytesThisChar;
      }

      // Double check that the number of expected bytes
      // was generated.
      bytesTotal = byteIndex;

      if (bytes.length != bytesTotal) {
        throw new TclRuntimeError(
            "generated " + bytes.length + " but expected to generate " + bytesTotal + " bytes");
      }

      // Count Utf8 bytes for each character, map byte to char index

      byteToCharIndex = new int[bytes.length];
      for (charIndex = 0, byteIndex = 0, bytesThisChar = 0;
          byteIndex < byteToCharIndex.length;
          byteIndex++, bytesThisChar--) {
        if (byteIndex > 0 && bytesThisChar == 0) {
          charIndex++;
        }
        byteToCharIndex[byteIndex] = charIndex;

        c = getArray()[charIndex];
        if (bytesThisChar == 0) {
          if (c == '\0') {
            // Ignore encoding issues related to null byte in Java
            // vs UTF8
            bytesThisChar = 1;
          } else {
            bytesThisChar = StringCmd.Utf8Count(c);
          }
        }
      }
    } catch (java.io.UnsupportedEncodingException ex) {
      throw new TclRuntimeError("UTF8 encoding not supported");
    }
  }

  // Return bytes in the given byte range as a String

  public String getByteRangeAsString(int byteIndex, int byteLength) {
    if (bytes == null) {
      // One byte for each character
      return orig.substring(byteIndex, byteIndex + byteLength);
    }

    try {
      return new String(bytes, byteIndex, byteLength, "UTF8");
    } catch (java.io.UnsupportedEncodingException ex) {
      throw new TclRuntimeError("UTF8 encoding not supported");
    }
  }

  /**
   * Convert char index into a byte index.
   *
   * @param charIndex
   * @return
   */
  public int getByteIndex(int charIndex) {
    if (bytes == null) {
      // One byte for each character
      return charIndex;
    }

    return charToByteIndex[charIndex];
  }

  /**
   * Given a char index and range, return the number of bytes in the range.
   *
   * @param charIndex
   * @param charRange
   * @return
   */
  public int getByteRange(int charIndex, int charRange) {
    if (bytes == null) {
      // One byte for each character
      return charRange;
    }

    return charToByteIndex[charIndex + charRange] - charToByteIndex[charIndex];
  }

  // Get number of bytes for the given char index

  public int getBytesAtIndex(int charIndex) {
    if (bytes == null) {
      // One byte for each character
      return 1;
    }

    return charToByteIndex[charIndex + 1] - charToByteIndex[charIndex];
  }

  /**
   * Return length of script in bytes
   *
   * @return
   */
  public int getByteLength() {
    if (bytes == null) {
      // One byte for each character
      return orig.length();
    }

    return bytes.length - 1;
  }

  /**
   * Given a byte index, return the char index.
   *
   * @param byteIndex
   * @return
   */
  public int getCharIndex(int byteIndex) {
    if (bytes == null) {
      // One byte for each character
      return byteIndex;
    }

    return byteToCharIndex[byteIndex];
  }

  // Given a byte index and range, return the number of
  // chars in the range.

  public int getCharRange(int byteIndex, int byteRange) {
    if (bytes == null) {
      // One byte for each character
      return byteRange;
    }

    return byteToCharIndex[byteIndex + byteRange] - byteToCharIndex[byteIndex];
  }

  // This API is used for debugging, it would never be invoked as part
  // of the InternalRep interface since a TclObject would always have
  // a string rep when the UTF8CharPointer is created and it should
  // never be invalidated.

  public String toString() {
    if (bytes == null) {
      // One byte for each character
      return "1 byte for each character with length " + orig.length();
    }

    StringBuffer sb = new StringBuffer();

    int max_char = getArray().length - 1;
    int max_byte = bytes.length - 1;
    int max = max_char;
    if (max_byte > max) {
      max = max_byte;
    }
    sb.append("index char/byte array: (sizes = " + max_char + " " + max_byte + ")\n");

    for (int i = 0; i < max; i++) {
      String char_ind = "   ", byte_ind = "   ";
      if (i < max_char) {
        char_ind = "'" + getArray()[i] + "'";
      }
      if (i < max_byte) {
        byte_ind = "'" + ((char) bytes[i]) + "'";
      }

      sb.append("[" + i + "] = " + char_ind + " " + byte_ind + "\n");
    }
    sb.append("\n");

    sb.append("charToByteIndex array:\n");
    for (int i = 0; i < charToByteIndex.length - 1; i++) {
      sb.append("[" + i + "] = " + charToByteIndex[i] + "\n");
    }
    sb.append("\n");

    sb.append("byteToCharIndex array:\n");
    for (int i = 0; i < byteToCharIndex.length - 1; i++) {
      sb.append("[" + i + "] = " + byteToCharIndex[i] + "\n");
    }
    sb.append("\n");

    return sb.toString();
  }

  // InternalRep interfaces

  // Called to free any storage for the type's internal rep.

  public void dispose() {}

  // duplicate

  public InternalRep duplicate() {
    // A UTF8CharPointer is read-only, so just dup the ref
    return this;
  }
}
