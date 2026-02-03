/*
 * TclEvent.java --
 *
 *	Abstract class for describing an event in the Tcl notifier
 *	API.
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 * RCS: @(#) $Id: TclEvent.java,v 1.3 2003/03/11 01:45:53 mdejong Exp $
 *
 */

package tcl.lang;

import tcl.lang.exception.TclRuntimeError;

/**
 * This is an abstract class that describes an event in the Jacl implementation of the notifier. It
 * contains package protected fields and methods that are accessed by the Jacl notifier. Tcl Blend
 * needs a different implementation of the TclEvent base class.
 *
 * <p>The only public methods in this class are processEvent() and sync(). These methods must appear
 * in both the Jacl and Tcl Blend versions of this class.
 */
public abstract class TclEvent {

  private Notifier notifier = null;

  private boolean needsNotify = false;

  private boolean isProcessing = false;

  private boolean isProcessed = false;

  private TclEvent next;

  /** Process the event. Override this method to implement new types of events. */
  public abstract int processEvent(int flags);

  /** Wait until this event has been processed. */
  public final void sync() {
    if (getNotifier() == null) {
      throw new TclRuntimeError("TclEvent is not queued when sync() is called");
    }

    if (Thread.currentThread() == getNotifier().getPrimaryThread()) {
      while (!isProcessed()) {
        getNotifier().serviceEvent(0);
      }
    } else {
      synchronized (this) {
        setNeedsNotify(true);
        while (!isProcessed()) {
          try {
            wait(0);
          } catch (InterruptedException e) {
            continue;
          }
        }
      }
    }
  }

  /** The notifier in which this event is queued. */
  public Notifier getNotifier() {
    return notifier;
  }

  public void setNotifier(Notifier notifier) {
    this.notifier = notifier;
  }

  /** This flag is true if sync() has been called on this object. */
  public boolean isNeedsNotify() {
    return needsNotify;
  }

  public void setNeedsNotify(boolean needsNotify) {
    this.needsNotify = needsNotify;
  }

  /**
   * True if this event is current being processing. This flag provents an event to be processed
   * twice when the event loop is entered recursively.
   */
  public boolean isProcessing() {
    return isProcessing;
  }

  public void setProcessing(boolean processing) {
    isProcessing = processing;
  }

  /** True if this event has been processed. */
  public boolean isProcessed() {
    return isProcessed;
  }

  public void setProcessed(boolean processed) {
    isProcessed = processed;
  }

  /** Links to the next event in the event queue. */
  public TclEvent getNext() {
    return next;
  }

  public void setNext(TclEvent next) {
    this.next = next;
  }
} // end TclEvent
