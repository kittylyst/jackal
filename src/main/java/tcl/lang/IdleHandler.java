/*
 * IdleHandler.java --
 *
 *	The API for defining idle event handler.
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: IdleHandler.java,v 1.3 2006/06/12 04:00:18 mdejong Exp $
 *
 */

package tcl.lang;

/** This abstract class is used to define idle handlers. */
public abstract class IdleHandler {

  private Notifier notifier;

  private boolean isCancelled;

  private int generation;

  /** Create an idle handler. Must call register(Notifier) before the handler will be called. */
  public IdleHandler() {
    setCancelled(false);
    setGeneration(0);
    setNotifier(null);
  }

  /**
   * Create a idle handler to be fired when the notifier is idle. Side effects: The idle is
   * registered in the list of idle handlers in the given notifier. When the notifier is idle, the
   * processIdleEvent() method will be invoked exactly once inside the primary thread of the
   * notifier.
   *
   * @param n the notifier to fire the event
   */
  public IdleHandler(Notifier n) {
    register(n);
  }

  /**
   * Register Idle handler in the list of handlers for the given Notifier
   *
   * @param n the notifier to fire the events
   */
  public void register(Notifier n) {
    setNotifier((Notifier) n);
    setCancelled(false);

    synchronized (getNotifier()) {
      getNotifier().idleList.add(this);
      setGeneration(getNotifier().idleGeneration);
      if (Thread.currentThread() != getNotifier().primaryThread) {
        getNotifier().signalWaiters();
      }
    }
  }

  /**
   * Mark this idle handler as cancelled so that it won't be invoked. Side effects: The idle handler
   * is marked as cancelled so that its processIdleEvent() method will not be called. If the idle
   * event has already fired, then nothing this call has no effect.
   */
  public synchronized void cancel() {
    if (isCancelled()) {
      return;
    }

    setCancelled(true);

    synchronized (getNotifier()) {
      for (int i = 0; i < getNotifier().idleList.size(); i++) {
        if (getNotifier().idleList.get(i) == this) {
          getNotifier().idleList.remove(i);

          /*
           * We can return now because the same idle handler can be
           * registered only once in the list of idles.
           */

          return;
        }
      }
    }
  }

  /**
   * Execute the idle handler if it has not been cancelled. This method should be called by the
   * notifier only.
   *
   * <p>Because the idle handler may be being cancelled by another thread, both this method and
   * cancel() must be synchronized to ensure correctness.
   *
   * @return 0 if the handler was not executed because it was already cancelled, 1 otherwise.
   */
  final synchronized int invoke() {
    /*
     * The idle handler may be cancelled after it was registered in the
     * notifier. Check the isCancelled field to make sure it's not
     * cancelled.
     */

    if (!isCancelled()) {
      processIdleEvent();
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * This method is called when the idle is expired. Override This method to implement your own idle
   * handlers.
   */
  public abstract void processIdleEvent();

  /** This method prints a text description of the event for debugging. */
  public String toString() {
    StringBuffer sb = new StringBuffer(64);
    sb.append("IdleHandler.generation is " + getGeneration() + "\n");
    return sb.toString();
  }

  /** Back pointer to the notifier that will fire this idle. */
  public Notifier getNotifier() {
    return notifier;
  }

  public void setNotifier(Notifier notifier) {
    this.notifier = notifier;
  }

  /** True if the cancel() method has been called. */
  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  /** Used to distinguish older idle handlers from recently-created ones. */
  public int getGeneration() {
    return generation;
  }

  public void setGeneration(int generation) {
    this.generation = generation;
  }
}
