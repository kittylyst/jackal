package tcl.lang.cmd;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class SubstCmdTest extends TclCmdTest {
  @Test
  public void testCmd() throws Exception {
    String resName = "/tcl/lang/cmd/subst.test";
    tclTestResource(TCLTEST_NAMEOFEXECUTABLE, resName, Collections.EMPTY_LIST);
  }
}
