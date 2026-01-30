package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class SourceCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/source.test";
    tclTestResource(resName);
  }
}
