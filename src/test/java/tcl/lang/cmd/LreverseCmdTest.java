package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class LreverseCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/lreverse.test";
    tclTestResource(resName);
  }
}
