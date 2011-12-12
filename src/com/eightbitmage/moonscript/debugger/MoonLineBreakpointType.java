/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.eightbitmage.moonscript.debugger;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.util.MoonFileUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/26/11
 * Time: 3:04 PM
 */
public class MoonLineBreakpointType extends XLineBreakpointType {
    private static final Logger log = Logger.getInstance("Lua.MoonLineBreakpointType");

    private final MoonDebuggerEditorsProvider myEditorsProvider = new MoonDebuggerEditorsProvider();
    
    public MoonLineBreakpointType() {
        super("lua-line", "Lua Line Breakpoints");
    }

    @Override
    public XBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return null;
    }

    @Override
    public String getDisplayText(XBreakpoint breakpoint) {
        XSourcePosition sourcePosition = breakpoint.getSourcePosition();

        assert sourcePosition != null;
        return "Line " + String.valueOf(sourcePosition.getLine()) +
                " in file " + MoonFileUtil.getPathToDisplay(sourcePosition.getFile());
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        // TODO: scan the line looking for a statement START
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

        assert psiFile != null;

        if (!(psiFile instanceof MoonPsiFile)) return false;
        
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        assert document != null;

        int start = document.getLineStartOffset(line);
        int end = document.getLineEndOffset(line);

        for (MoonStatementElement stat : ((MoonPsiFile) psiFile).getAllStatements())
            if (stat.getTextOffset() >= start && stat.getTextOffset() < end)
                return true;

        return false;
    }


    public XDebuggerEditorsProvider getEditorsProvider()
    {
        return myEditorsProvider;
    }
}
