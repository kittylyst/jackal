/*
 * TclParse.java --
 *
 * 	A Class of the following type is filled in by Parser.parseCommand.
 * 	It describes a single command parsed from an input string.
 *
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 * RCS: @(#) $Id: TclParse.java,v 1.5 2006/03/27 21:42:55 mdejong Exp $
 */

package tcl.lang.parse;

import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.exception.TclException;
import tcl.lang.model.TclInteger;
import tcl.lang.model.TclList;
import tcl.lang.model.TclObject;
import tcl.lang.model.TclString;

public final class TclParse {

  private final char[] chars;

  private final int endIndex;

  private int commentStart = -1;

  private int commentSize = 0;

  private int commandStart = -1;

  private int commandSize = 0;

  private int numWords = 0;

  private TclToken[] tokenList = new TclToken[INITIAL_NUM_TOKENS];

  private int numTokens = 0;

  private int tokensAvailable = INITIAL_NUM_TOKENS;

  private int errorType = Parser.TCL_PARSE_SUCCESS;

  /*
   * ----------------------------------------------------------------------
   *
   * The fields below are intended only for the private use of the parser.
   * They should not be used by procedures that invoke Tcl_ParseCommand.
   *
   * ----------------------------------------------------------------------
   */

  // Interpreter to use for error reporting, or null.

  private final Interp interp;

  // Name of file from which script came, or null. Used for error
  // messages.

  private final String fileName;

  // Line number corresponding to first character in string.

  private int lineNum;

  // Points to character in string that terminated most recent token.
  // Filled in by Parser.parseTokens. If an error occurs, points to
  // beginning of region where the error occurred (e.g. the open brace
  // if the close brace is missing).

  private int termIndex;

  // This field is set to true by Parser.parseCommand if the command
  // appears to be incomplete. This information is used by
  // Parser.commandComplete.

  private boolean incomplete = false;

  // When a TclParse is the return value of a method, result is set to
  // a standard Tcl result, indicating the return of the method.

  private int result = TCL.OK;

  // Extra integer field used to return a value in a parse operation.

  private int extra;

  // Default size of the tokenList array.
  private static final int INITIAL_NUM_TOKENS = 20;
  private static final boolean USE_TOKEN_CACHE = true;
  private static final int MAX_CACHED_TOKENS = 50; // my tests show 50 is best

  /**
   * Construct a TclParse object with default values.
   *
   * @param interp Interpreter to use for error reporting, if null, then no error message is
   *     provided
   * @param chars The command being parsed
   * @param endIndex Offset of the char after the last valid command character
   * @param fileName Name of file being executed, or null
   * @param lineNum Line number of file; used for error messages so it may be invalid
   */
  TclParse(Interp interp, char[] chars, int endIndex, String fileName, int lineNum) {
    this.interp = interp;
    this.chars = chars;
    this.endIndex = endIndex;
    this.fileName = fileName;
    this.lineNum = lineNum;
    this.setTermIndex(endIndex);
  }

  /*
   * ----------------------------------------------------------------------
   *
   * getToken --
   *
   * Gets the token from tokenList at the specified index. If the index is
   * greater than tokensAvailable, then increase the size of tokenList. If the
   * object at index is null create a new TclToken.
   *
   * Results: Returns the TclToken object referenced by tokenList[index].
   *
   * Side effects: The tokenList size may be expanded and/or a new TclToken
   * created.
   *
   * ----------------------------------------------------------------------
   */

  final TclToken getToken(int index) // The index into tokenList.
      {
    if (index >= getTokensAvailable()) {
      expandTokenArray(index);
    }

    if (getTokenList()[index] == null) {
      getTokenList()[index] = grabToken();
    }
    return getTokenList()[index];
  }

  // Release internal resources that this TclParser object might have
  // allocated

  void release() {
    // Release tokens in reverse order so that the newest
    // (possibly just allocated with new) tokens are returned
    // to the pool first.

    for (int index = getTokensAvailable() - 1; index >= 0; index--) {
      if (getTokenList()[index] != null) {
        releaseToken(getTokenList()[index]);
        getTokenList()[index] = null;
      }
    }
  }

  // Creating an interpreter will cause this init method to be called

  public static void init(Interp interp) {
    if (USE_TOKEN_CACHE) {
      TclToken[] TOKEN_CACHE = new TclToken[MAX_CACHED_TOKENS];
      for (int i = 0; i < MAX_CACHED_TOKENS; i++) {
        TOKEN_CACHE[i] = new TclToken();
      }

      interp.setParserTokens(TOKEN_CACHE);
      interp.setParserTokensUsed(0);
    }
  }

  private TclToken grabToken() {
    if (USE_TOKEN_CACHE) {
      if (getInterp() == null || getInterp().getParserTokensUsed() == MAX_CACHED_TOKENS) {
        // either we do not have a cache because the interp is null or
        // we have already
        // used up all the open cache slots, we just allocate a new one
        // in this case
        return new TclToken();
      } else {
        // the cache has an avaliable slot so grab it
        //          return interp.parserTokens[interp.parserTokensUsed++];
        var tmpUsed = getInterp().getParserTokensUsed();
        var tmpToken = getInterp().getParserTokens()[tmpUsed];
        getInterp().setParserTokensUsed(tmpUsed + 1);
        return tmpToken;
      }
    } else {
      return new TclToken();
    }
  }

  private final void releaseToken(TclToken token) {
    if (USE_TOKEN_CACHE) {
      if (getInterp() != null && getInterp().getParserTokensUsed() > 0) {
        // if cache is not full put the object back in the cache
        //          interp.parserTokens[--interp.parserTokensUsed] = token;
        var tmpUsed = getInterp().getParserTokensUsed() - 1;
        getInterp().getParserTokens()[tmpUsed] = token;
        getInterp().setParserTokensUsed(tmpUsed);
      }
    }
  }

  /*
   * ----------------------------------------------------------------------
   *
   * expandTokenArray --
   *
   * If the number of TclTokens in tokenList exceeds tokensAvailable, the
   * double the number number of available tokens, allocate a new array, and
   * copy all the TclToken over.
   *
   * Results: None.
   *
   * Side effects: Variable tokensAvailable doubles as well as the size of
   * tokenList.
   *
   * ----------------------------------------------------------------------
   */

  void expandTokenArray(int needed) {
    // Make sure there is at least enough room for needed tokens
    while (needed >= getTokensAvailable()) {
      setTokensAvailable(getTokensAvailable() * 2);
    }

    TclToken[] newList = new TclToken[getTokensAvailable()];
    System.arraycopy(getTokenList(), 0, newList, 0, getTokenList().length);
    setTokenList(newList);
  }

  /*
   * ----------------------------------------------------------------------
   *
   * insertInTokenArray --
   *
   * This helper method will expand the token array as needed and insert new
   * tokens in the array. This method is inlined in the C impl, but broken out
   * into a helper method here for the sake of simplicity.
   *
   * Results: None.
   *
   * Side effects: Can update the size of the token array.
   *
   * ----------------------------------------------------------------------
   */

  void insertInTokenArray(int location, int numNew) {
    int needed = getNumTokens() + numNew;
    if (needed > getTokensAvailable()) {
      expandTokenArray(needed);
    }

    System.arraycopy(
        getTokenList(),
        location,
        getTokenList(),
        location + numNew,
        getTokenList().length - location - numNew);
    for (int i = 0; i < numNew; i++) {
      getTokenList()[location + i] = grabToken();
    }
  }

  /*
   * ----------------------------------------------------------------------
   *
   * toString --
   *
   * Generate debug info on the structure of this Class
   *
   * Results: A String containing the debug info.
   *
   * Side effects: None.
   *
   * ----------------------------------------------------------------------
   */

  public String toString() {
    return (get().toString());
  }

  /*
   * ----------------------------------------------------------------------
   *
   * get --
   *
   * get a TclObject that has a string representation of this object
   *
   * Results: |>None.<|
   *
   * Side effects: |>None.<|
   *
   * ----------------------------------------------------------------------
   */

  TclObject get() {
    TclObject obj;
    TclToken token;
    String typeString;
    int nextIndex;
    String cmd;
    int i;

    final boolean debug = false;

    if (debug) {
      System.out.println();
      System.out.println("Entered TclParse.get()");
      System.out.println("numTokens is " + getNumTokens());
    }

    obj = TclList.newInstance();
    try {
      if (getCommentSize() > 0) {
        TclList.append(
            getInterp(),
            obj,
            TclString.newInstance(new String(getChars(), getCommentStart(), getCommentSize())));
      } else {
        TclList.append(getInterp(), obj, TclString.newInstance("-"));
      }

      if (getCommandStart() >= (getEndIndex() + 1)) {
        setCommandStart(getEndIndex());
      }
      cmd = new String(getChars(), getCommandStart(), getCommandSize());
      TclList.append(getInterp(), obj, TclString.newInstance(cmd));
      TclList.append(getInterp(), obj, TclInteger.newInstance(getNumWords()));

      for (i = 0; i < getNumTokens(); i++) {
        if (debug) {
          System.out.println("processing token " + i);
        }

        token = getTokenList()[i];
        switch (token.type) {
          case Parser.TCL_TOKEN_WORD:
            typeString = "word";
            break;
          case Parser.TCL_TOKEN_SIMPLE_WORD:
            typeString = "simple";
            break;
          case Parser.TCL_TOKEN_TEXT:
            typeString = "text";
            break;
          case Parser.TCL_TOKEN_BS:
            typeString = "backslash";
            break;
          case Parser.TCL_TOKEN_COMMAND:
            typeString = "command";
            break;
          case Parser.TCL_TOKEN_VARIABLE:
            typeString = "variable";
            break;
          default:
            typeString = "??";
            break;
        }

        if (debug) {
          System.out.println("typeString is " + typeString);
        }

        TclList.append(getInterp(), obj, TclString.newInstance(typeString));
        TclList.append(getInterp(), obj, TclString.newInstance(token.getTokenString()));
        TclList.append(getInterp(), obj, TclInteger.newInstance(token.numComponents));
      }
      nextIndex = getCommandStart() + getCommandSize();
      TclList.append(
          getInterp(),
          obj,
          TclString.newInstance(new String(getChars(), nextIndex, (getEndIndex() - nextIndex))));

    } catch (TclException e) {
      // Do Nothing.
    }

    return obj;
  }

  public Interp getInterp() {
    return interp;
  }

  /** The original command string passed to Parser.parseCommand. */
  public char[] getChars() {
    return chars;
  }

  /** Index into 'string' that is the character just after the last one in the command string. */
  public int getEndIndex() {
    return endIndex;
  }

  /** Stores the tokens that compose the command. */
  public TclToken[] getTokenList() {
    return tokenList;
  }

  public void setTokenList(TclToken[] tokenList) {
    this.tokenList = tokenList;
  }

  /** Total number of tokens in command. */
  public int getNumTokens() {
    return numTokens;
  }

  public void setNumTokens(int numTokens) {
    this.numTokens = numTokens;
  }

  public String getFileName() {
    return fileName;
  }

  public int getLineNum() {
    return lineNum;
  }

  /**
   * Index into 'string' that is the # that begins the first of one or more comments preceding the
   * command.
   */
  public int getCommentStart() {
    return commentStart;
  }

  public void setCommentStart(int commentStart) {
    this.commentStart = commentStart;
  }

  /**
   * Number of bytes in comments (up through newline character that terminates the last comment). If
   * there were no comments, this field is 0.
   */
  public int getCommentSize() {
    return commentSize;
  }

  public void setCommentSize(int commentSize) {
    this.commentSize = commentSize;
  }

  /** Index into 'string' that is the first character in first // word of command. */
  public int getCommandStart() {
    return commandStart;
  }

  public void setCommandStart(int commandStart) {
    this.commandStart = commandStart;
  }

  /**
   * // Number of bytes in command, including first character of // first word, up through the
   * terminating newline, close // bracket, or semicolon.
   */
  public int getCommandSize() {
    return commandSize;
  }

  public void setCommandSize(int commandSize) {
    this.commandSize = commandSize;
  }

  /** Total number of words in command. May be 0. */
  public int getNumWords() {
    return numWords;
  }

  public void setNumWords(int numWords) {
    this.numWords = numWords;
  }

  /** Total number of tokens available at token. */
  public int getTokensAvailable() {
    return tokensAvailable;
  }

  public void setTokensAvailable(int tokensAvailable) {
    this.tokensAvailable = tokensAvailable;
  }

  /** One of the parsing error types defined in Parser class. */
  public int getErrorType() {
    return errorType;
  }

  public void setErrorType(int errorType) {
    this.errorType = errorType;
  }

  public int getTermIndex() {
    return termIndex;
  }

  public void setTermIndex(int termIndex) {
    this.termIndex = termIndex;
  }

  public boolean isIncomplete() {
    return incomplete;
  }

  public void setIncomplete(boolean incomplete) {
    this.incomplete = incomplete;
  }

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public int getExtra() {
    return extra;
  }

  public void setExtra(int extra) {
    this.extra = extra;
  }
}
