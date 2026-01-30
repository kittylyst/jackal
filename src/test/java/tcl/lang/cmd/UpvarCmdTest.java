
package tcl.lang.cmd;

import java.util.Arrays;
import java.util.LinkedList;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class UpvarCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		LinkedList expectedFailureList = new LinkedList(Arrays.asList( new String[] {
				// these test pass, except order of list returned by "array names" if different
				"upvar-3.5", "upvar-3.6"
			}));
		
		String resName = "/tcl/lang/cmd/upvar.test";
		tclTestResource(resName, expectedFailureList);
	}
}
