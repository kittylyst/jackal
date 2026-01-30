package tcl.lang.cmd;

import java.util.Arrays;
import java.util.LinkedList;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class WhileCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
			// widespread, pesky "invoked from within" instead of "while executing" in error message
			"while-1.2", "while-1.8"
		}));
			
		String resName = "/tcl/lang/cmd/while.test";
		tclTestResource(resName, expectedFailureList);
	}
}
