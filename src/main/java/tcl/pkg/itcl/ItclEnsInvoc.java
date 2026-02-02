package tcl.pkg.itcl;

import tcl.lang.InternalRep;
import tcl.lang.model.TclObject;

/**
 * // This class defines a Tcl object type that takes the // place of a part name during ensemble
 * invocations. When an // error occurs and the caller tries to print objv[0], it will // get a
 * string that contains a complete path to the ensemble // part.
 */
public final class ItclEnsInvoc implements InternalRep /* , CommandWithDispose */ {
  EnsemblePart ensPart;
  TclObject chainObj;

  // Implement InternalRep interface
  // Note: SetEnsInvocFromAny is not used

  @Override
  public InternalRep duplicate() {
    return Ensemble.DupEnsInvocInternalRep(this);
  }

  @Override
  public void dispose() {
    Ensemble.FreeEnsInvocInternalRep(this);
  }

  public String toString() {
    return Ensemble.UpdateStringOfEnsInvoc(this);
  }

  public static TclObject newInstance() {
    return new TclObject(new ItclEnsInvoc());
  }

  /*
   * // Implement CommandWithDispose interface
   *
   * public void cmdProc(Interp interp, TclObject argv[]) throws TclException
   * {}
   *
   * public void disposeCmd() {}
   */
}
