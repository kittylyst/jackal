package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class LsetCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/lset.test";
    tclTestResource(resName);
  }

  @Test
  public void testLsetComp() throws Exception {
    String resName = "/tcl/lang/cmd/lsetComp.test";
    tclTestResource(resName);
  }
}
