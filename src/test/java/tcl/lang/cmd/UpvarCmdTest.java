package tcl.lang.cmd;

import java.util.Collections;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class UpvarCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/upvar.test";
    tclTestResource(resName, Collections.emptyList());
  }
}
