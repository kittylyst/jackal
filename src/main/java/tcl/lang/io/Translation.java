package tcl.lang.io;

import tcl.lang.TclIO;

/**
 * Translate \n, \r, \r\n to \n on input; translate \n to platform-specific end of line on output
 */
public enum Translation {
  TRANS_AUTO(0),

  /** Don't translate end of line characters */
  TRANS_BINARY(1),

  /** Don't translate end of line characters */
  TRANS_LF(2),

  /** \n -> \r on output; \r -> \n on input */
  TRANS_CR(3),

  /** \n to \r\n on output; \r\n -> \n on input */
  TRANS_CRLF(4);

  private final int code;

  Translation(int val) {
    code = val;
  }

  /**
   * @param translation one the fconfigure -translation strings
   * @return a numerical identifier for the given -translation string.
   */
  public static Translation getTranslation(String translation) throws IllegalArgumentException {
    return switch (translation) {
      case "auto" -> TRANS_AUTO;
      case "cr" -> TRANS_CR;
      case "crlf" -> TRANS_CRLF;
      case "lf" -> TRANS_LF;
      case "binary" -> TRANS_LF;
      case "platform" -> TclIO.getTransPlatform();
      default ->
          throw new IllegalArgumentException(
              "bad value for -translation: must be one of auto, binary, cr, lf, crlf, or platform");
    };
  }

  /**
   * @param translation one of the TRANS_* constances
   * @return a string description for a translation id defined above.
   */
  public static String getString(Translation translation) {
    return switch (translation) {
      case TRANS_AUTO -> "auto";
      case TRANS_CR -> "cr";
      case TRANS_CRLF -> "crlf";
      case TRANS_LF, TRANS_BINARY -> "lf";
    };
  }

  public int getCode() {
    return code;
  }
}
