package tcl.lang.cmd;

import java.util.Collections;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class LlengthCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/llength.test";
		tclTestResource(resName);
	}
}
