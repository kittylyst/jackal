package tcl.lang.cmd;

import java.util.Collections;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class SubstCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/subst.test";
		tclTestResource(TCLTEST_NAMEOFEXECUTABLE,  resName, Collections.EMPTY_LIST);
	}
}
