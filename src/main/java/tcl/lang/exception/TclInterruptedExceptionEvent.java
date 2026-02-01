package tcl.lang.exception;

import tcl.lang.EventDeleter;
import tcl.lang.Interp;
import tcl.lang.TclEvent;

/**
 * // This class implements an event that will raise // a TclInterruptedException in the interp.
 * This // event is queued from a thread other the one // the interp is executing in, so be careful
 * // not to interact with the interp since it // would not be thread safe. This event will // wake
 * up a Jacl thread waiting in a vwait // or in the main processing loop. This class is // used only
 * in Jacl's Interp implementaton.
 */
public class TclInterruptedExceptionEvent extends TclEvent implements EventDeleter {
  private Interp interp;
  private boolean wasProcessed;
  private boolean exceptionRaised;

  public TclInterruptedExceptionEvent(Interp interp) {
    this.setInterp(interp);
    this.setWasProcessed(false);
    this.setExceptionRaised(false);
  }

  // processEvent() is invoked in the interp thread,
  // so this code can interact with the interp.

  public int processEvent(int flags) {
    setWasProcessed(true);
    getInterp().checkInterrupted();

    // Should never reach here since
    // an earlier call to setInterrupted()
    // would cause checkInterrupted() to
    // raise a TclInterruptedException.
    // return code just makes the compiler happy.

    return 1;
  }

  // Implement EventDeleter so that this event can
  // delete itself from the pending event queue.
  // This method returns 1 when an event should
  // be deleted from the queue.

  public int deleteEvent(TclEvent evt) {
    if (evt == this) {
      return 1;
    }
    return 0;
  }

  public Interp getInterp() {
    return interp;
  }

  public void setInterp(Interp interp) {
    this.interp = interp;
  }

  public boolean isWasProcessed() {
    return wasProcessed;
  }

  public void setWasProcessed(boolean wasProcessed) {
    this.wasProcessed = wasProcessed;
  }

  public boolean isExceptionRaised() {
    return exceptionRaised;
  }

  public void setExceptionRaised(boolean exceptionRaised) {
    this.exceptionRaised = exceptionRaised;
  }
}
