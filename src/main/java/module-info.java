/**
 * Named module for the Jackal/JTcl Tcl implementation. Enables sealed class hierarchies that span
 * packages (e.g. TclEvent, Extension, TimerHandler, IdleHandler).
 */
module jackal {
  requires java.base;
  requires java.desktop; // java.beans (Tcl Blend / Java integration)
  requires java.scripting; // javax.script (JSR 223)

  exports tcl.lang;
  exports tcl.lang.channel;
  exports tcl.lang.cmd;
  exports tcl.lang.process;
  exports tcl.lang.embed.jsr223;
  exports tcl.pkg.fleet;
  exports tcl.pkg.tjc;
  exports tcl.pkg.java;
  exports tcl.pkg.java.reflect;
  exports tcl.pkg.itcl;
}
