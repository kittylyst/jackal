package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class UplevelCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/uplevel.test";
    tclTestResource(resName);
  }
}
