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
package com.eightbitmage.moonscript.editor.inspections.performance;

import com.eightbitmage.moonscript.editor.inspections.AbstractInspection;
import com.eightbitmage.moonscript.editor.inspections.utils.ControlFlowUtils;
import com.eightbitmage.moonscript.lang.lexer.LuaTokenTypes;
import com.eightbitmage.moonscript.lang.psi.LuaPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaIdentifierList;
import com.eightbitmage.moonscript.lang.psi.statements.LuaAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaSymbol;
import com.eightbitmage.moonscript.lang.psi.visitor.LuaElementVisitor;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class StringConcatenationInLoopsInspection extends AbstractInspection {

    /**
     * @noinspection PublicField
     */
    public boolean m_ignoreUnlessAssigned = true;

    @Override
    @NotNull
    public String getDisplayName() {
        return "String concatenation in a loop";
    }

    //    @Override
    @NotNull
    protected String buildErrorString(Object... infos) {
        return  "String concatenation in loop";
    }

    @NotNull
    @Override
    public String getGroupDisplayName() {
        return PERFORMANCE_ISSUES;
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public LuaElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new LuaElementVisitor() {

            @Override
            public void visitBinaryExpression(
                    LuaBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                if (expression.getRightOperand() == null) {
                    return;
                }
                final LuaPsiElement sign = expression.getOperator();
                final IElementType tokenType = sign.getNode().getFirstChildNode().getElementType();
                if (!tokenType.equals(LuaTokenTypes.CONCAT)) {
                    return;
                }
                if (!ControlFlowUtils.isInLoop(expression)) {
                    return;
                }

                PsiElement e = expression.getParent().getParent();
                if (!(e instanceof LuaAssignmentStatement))
                    return;

                LuaIdentifierList lvalues = ((LuaAssignmentStatement) e).getLeftExprs();

                if (lvalues == null || lvalues.count() != 1)
                    return;

                LuaSymbol id = lvalues.getSymbols()[0];

                if (!id.getText().equals(expression.getLeftOperand().getText()))
                    return;

                holder.registerProblem(expression, buildErrorString(), LocalQuickFix.EMPTY_ARRAY);
            }
        };
    }
}
