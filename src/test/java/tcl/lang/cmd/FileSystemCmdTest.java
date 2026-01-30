package tcl.lang.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import tcl.lang.TclCmdTest;
import org.junit.jupiter.api.Test;

public class FileSystemCmdTest extends TclCmdTest {
	@Test
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/fileSystem.test";
		tclTestResource(resName,Collections.EMPTY_LIST);
	}
}