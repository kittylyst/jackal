package tcl.lang.cmd;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class LassignCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/lassign.test";
		tclTestResource(resName);
	}
}