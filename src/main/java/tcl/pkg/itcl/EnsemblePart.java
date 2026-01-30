package tcl.pkg.itcl;

import tcl.lang.Command;
import tcl.lang.WrappedCommand;

public record EnsemblePart(
    String name, // name of this part
    int minChars, // chars needed to uniquely identify part
    Command cmd, // command handling this part
    WrappedCommand wcmd, // wrapped for command
    String usage, // usage string describing syntax
    Ensemble ensemble) // ensemble containing this part
{}
