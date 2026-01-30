package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class ConcatCmdText  extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/concat.test";
		tclTestResource(resName);
	}
}
