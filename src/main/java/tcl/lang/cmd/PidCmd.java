package tcl.lang.cmd;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclIO;
import tcl.lang.TclInteger;
import tcl.lang.TclList;
import tcl.lang.TclObject;
import tcl.lang.channel.Channel;
import tcl.lang.channel.PipelineChannel;
import tcl.lang.exception.TclException;

/**
 * implement TCL 'pid' command. JVM doesn't support PID queries directly so we play some tricks
 * here. If pid can't be found, -1 (or a list of -1) is returned
 *
 * @author danb
 */
public final class PidCmd implements Command {

  /**
   * @return PID of the TCL process
   */
  public static int getPid() {
    return (int) ProcessHandle.current().pid();
  }

  public void cmdProc(Interp interp, TclObject[] objv) throws TclException {
    if (objv.length == 1) {
      // return JVM pid
      interp.setResult(getPid());
    } else if (objv.length == 2) {
      // look up PIDs for a PipelineChannel
      Channel channel = TclIO.getChannel(interp, objv[1].toString());
      if (channel == null) {
        throw new TclException(interp, "can not find channel named \"" + objv[1].toString() + "\"");
      }
      if (channel instanceof PipelineChannel) {
        int[] pids = ((PipelineChannel) channel).getPipeline().getProcessIdentifiers();
        TclObject rv = TclList.newInstance();
        for (int pid : pids) {
          TclList.append(interp, rv, TclInteger.newInstance(pid));
        }
        interp.setResult(rv);
      } else {
        interp.setResult("");
      }
    } else {
      throw new TclException(interp, "wrong # args: should be \"pid ?channelId?\"");
    }
  }
}
