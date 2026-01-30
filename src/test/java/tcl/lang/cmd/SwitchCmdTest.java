package tcl.lang.cmd;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class SwitchCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/switch.test";
    tclTestResource(resName, Collections.EMPTY_LIST);
  }
}
