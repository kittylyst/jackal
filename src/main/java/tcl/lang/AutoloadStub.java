package tcl.lang;

import tcl.lang.exception.PackageNameException;
import tcl.lang.exception.TclException;
import tcl.lang.model.TclObject;

/**
 * The purpose of AutoloadStub is to load-on-demand the classes that implement Tcl commands. This
 * reduces Jacl start up time and, when running Jacl off a web page, reduces download time
 * significantly.
 */
public final class AutoloadStub implements Command {
  private String className;

  /**
   * Create a stub command which autoloads the real command the first time the stub command is
   * invoked.
   *
   * @param clsName name of the Java class that implements this command, e.g. "tcl.lang.AfterCmd"
   */
  public AutoloadStub(String clsName) {
    this.className = clsName;
  }

  /**
   * Load the class that implements the given command and execute it.
   *
   * @param interp the current interpreter.
   * @param objv command arguments.
   * @exception TclException if error happens inside the real command proc.
   */
  public void cmdProc(Interp interp, TclObject[] objv) throws TclException {
    Command cmd = load(interp, objv[0].toString());
    // don't call via WrappedCommand.invoke() because this cmdProc was already
    // called with invoke
    cmd.cmdProc(interp, objv);
  }

  /**
   * Load the class that implements the given command, create the command in the interpreter, and
   * return. This helper method is provided so to handle the case where a command wants to create a
   * stub command without executing it. The qname argument should be the fully qualified name of the
   * command.
   */
  public Command load(Interp interp, String qname) throws TclException {
    Class cmdClass = null;
    Command cmd;

    try {
      TclClassLoader classLoader = (TclClassLoader) interp.getClassLoader();
      cmdClass = classLoader.loadClass(getClassName());
    } catch (ClassNotFoundException e) {
      throw new TclException(interp, "ClassNotFoundException for class \"" + getClassName() + "\"");
    } catch (PackageNameException e) {
      throw new TclException(interp, "PackageNameException for class \"" + getClassName() + "\"");
    }

    try {
      cmd = (Command) cmdClass.newInstance();
    } catch (IllegalAccessException e1) {
      throw new TclException(
          interp, "IllegalAccessException for class \"" + cmdClass.getName() + "\"");
    } catch (InstantiationException e2) {
      throw new TclException(
          interp, "InstantiationException for class \"" + cmdClass.getName() + "\"");
    } catch (ClassCastException e3) {
      throw new TclException(interp, "ClassCastException for class \"" + cmdClass.getName() + "\"");
    }
    interp.createCommand(qname, cmd);
    return cmd;
  }

  public String getClassName() {
    return className;
  }

  //    public void setClassName(String className) {
  //        this.className = className;
  //    }
}
