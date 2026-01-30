package tcl.lang.cmd;

import org.junit.jupiter.api.Test;
import tcl.lang.TclCmdTest;

public class AppendCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/append.test";
		tclTestResource(resName);
	}
	
	@Test
	public void testAppendComp() throws Exception {
		String resName = "/tcl/lang/cmd/appendComp.test";
		tclTestResource(resName);
	}
}