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

package com.eightbitmage.moonscript.lang.psi.impl.statements;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFunctionCallExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionCallStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

//import com.eightbitmage.moonscript.lang.psi.expressions.LuaFunctionIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class MoonFunctionCallStatementImpl extends MoonStatementElementImpl implements MoonFunctionCallStatement {

    public MoonFunctionCallStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MoonFunctionCallExpression getInvokedExpression() {
        return findChildByClass(MoonFunctionCallExpression.class);
    }

    @Override
    public MoonExpressionList getArgumentList() {
        return getInvokedExpression().getArgumentList();
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitFunctionCallStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitFunctionCallStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String toString() {
        MoonFunctionCallExpression e = getInvokedExpression();

        return "Stmt: " + e.toString();
    }
}
