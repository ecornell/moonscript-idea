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

package com.eightbitmage.moonscript.editor.inspections.performance;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.editor.inspections.LuaFix;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonDeclarationStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonLocalDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/17/11
 * Time: 12:53 AM
 */
public class RedundantInitializationInspection extends AbstractInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Redundant Initialization";
    }


    @NotNull
    @Override
    public String getGroupDisplayName() {
        return PERFORMANCE_ISSUES;
    }

    @Override
    public String getStaticDescription() {
        return "Looks for unnecessary initialization of local variables";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.INFO;
    }


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new MoonElementVisitor() {
            @Override
            public void visitDeclarationStatement(MoonDeclarationStatement e) {
                super.visitDeclarationStatement(e);

                if (e instanceof MoonLocalDefinitionStatement) {
                    MoonIdentifierList left = ((MoonLocalDefinitionStatement) e).getLeftExprs();
                    MoonExpressionList right = ((MoonLocalDefinitionStatement) e).getRightExprs();

                    if (right == null || right.count() == 0)
                        return;

                    boolean allNil = true;
                    for (MoonExpression expr : right.getMoonExpressions())
                        if (!expr.getText().equals("nil")) {
                            allNil = false;
                            break;
                        }

                    if (allNil) {
                        LocalQuickFix[] fixes = {new RedundantInitializationFix()};
                        holder.registerProblem(e, "Redundant Initialization", fixes);
                    }
                }
            }
        };
    }

    private class RedundantInitializationFix extends LuaFix {

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor) throws IncorrectOperationException {
            final MoonAssignmentStatement assign = (MoonAssignmentStatement) descriptor.getPsiElement();

            assign.getOperatorElement().delete();
            assign.getRightExprs().delete();
        }

        @NotNull
        @Override
        public String getName() {
            return "Remove unnecessary initialization";
        }
    }
}
