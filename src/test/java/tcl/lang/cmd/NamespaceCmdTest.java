package tcl.lang.cmd;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class NamespaceCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/namespace.test";
    tclTestResource(resName, Collections.EMPTY_LIST);
  }
}
