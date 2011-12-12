package com.eightbitmage.moonscript.debugger;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import org.jetbrains.annotations.NotNull;

public class MoonLineBreakpointHandler extends XBreakpointHandler {
    protected MoonDebugProcess myDebugProcess;



    public MoonLineBreakpointHandler(MoonDebugProcess debugProcess) {
        super(MoonLineBreakpointType.class);
        myDebugProcess = debugProcess;
    }

    public void registerBreakpoint(@NotNull XBreakpoint xBreakpoint) {
        myDebugProcess.addBreakPoint(xBreakpoint);
    }

    public void unregisterBreakpoint(@NotNull XBreakpoint xBreakpoint, boolean temporary) {        
        myDebugProcess.removeBreakPoint(xBreakpoint);
    }

    

}
