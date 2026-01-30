package tcl.pkg.itcl;

/** Info needed for public/protected/private commands during class definition parsing. */
public record ProtectionCmdInfo(int pLevel, ItclObjectInfo info) {}
