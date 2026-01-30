package tcl.lang.cmd;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class StringCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/string.test";
		tclTestResource(resName);
	}
	
	@Test
	public void testStringComp() throws Exception {
		String resName = "/tcl/lang/cmd/stringComp.test";
		tclTestResource(resName);
	}
}
