package tcl.lang.io;

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

  public int getCode() {
    return code;
  }
}
