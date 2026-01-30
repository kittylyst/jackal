package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class PwdCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/pwd.test";
    tclTestResource(resName);
  }
}
