package tcl.lang.cmd;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class LsetCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/lset.test";
		tclTestResource(resName);
	}
	
	@Test
	public void testLsetComp() throws Exception {
		String resName = "/tcl/lang/cmd/lsetComp.test";
		tclTestResource(resName);
	}
}
