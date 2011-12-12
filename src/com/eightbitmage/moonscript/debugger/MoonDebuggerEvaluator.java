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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/15/11
 * Time: 5:07 AM
 */
public class MoonDebuggerEvaluator extends XDebuggerEvaluator {
    private static final Logger log = Logger.getInstance("Lua.MoonDebuggerEvaluator");

    private Project myProject;
    private MoonStackFrame moonStackFrame;
    private MoonDebuggerController myController;

    public MoonDebuggerEvaluator(Project myProject, MoonStackFrame moonStackFrame, MoonDebuggerController myController) {

        this.myProject = myProject;
        this.moonStackFrame = moonStackFrame;
        this.myController = myController;
    }

    @Override
    public void evaluate(@NotNull String expression, XEvaluationCallback callback,
                         @Nullable XSourcePosition expressionPosition) {

        log.debug("evaluating: " + expression);
        myController.execute(new MoonDebuggerController.CodeExecutionRequest("return " + expression, callback));
    }
}
