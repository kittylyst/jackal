package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class CaseCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/case.test";
		tclTestResource(resName);
	}
}
