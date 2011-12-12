/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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
package com.eightbitmage.moonscript.editor.inspections.validity;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.editor.inspections.utils.ControlFlowUtils;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LuaUnreachableStatementInspection extends AbstractInspection {

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return VALIDITY_ISSUES;
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Unreachable Statement";
    }

    @Nullable
    protected String buildErrorString(Object... args) {
        return "Unreachable statement #loc";

    }

    public boolean isEnabledByDefault() {
        return false;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MoonElementVisitor() {


            public void visitBlock(MoonBlock block) {
                super.visitBlock(block);
                
                MoonStatementElement[] statements = block.getStatements();
                for (int i = 0; i < statements.length - 1; i++) {
                    checkPair(statements[i], statements[i + 1]);
                }
            }


            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);

                if (file instanceof MoonPsiFile) {
                    MoonStatementElement[] statements = ((MoonPsiFile) file).getStatements();
                    for (int i = 0; i < statements.length - 1; i++) {
                        checkPair(statements[i], statements[i + 1]);
                    }
                }
            }

            private void checkPair(MoonStatementElement prev, MoonStatementElement statement) {
                if (!ControlFlowUtils.statementMayCompleteNormally(prev)) {
                    holder.registerProblem(statement,
                            buildErrorString(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }

            }
        };
    }
}
