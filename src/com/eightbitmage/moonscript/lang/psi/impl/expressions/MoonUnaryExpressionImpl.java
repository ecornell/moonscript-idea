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

package com.eightbitmage.moonscript.lang.psi.impl.expressions;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonUnaryExpression;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 11:40:09 PM
 */
public class MoonUnaryExpressionImpl extends MoonExpressionImpl implements MoonUnaryExpression {
    public MoonUnaryExpressionImpl(ASTNode node) {
        super(node);
    }


    @Override
    public String toString() {
        MoonExpression expression = getExpression();
        return super.toString() + " ( " + getOperator().getText() + " " +
               (expression != null ? expression.getText() : "err") + ")";
    }

    @Override
    public MoonPsiElement getOperator() {
        return (MoonPsiElement) findChildByType(MoonElementTypes.UNARY_OP);
    }

    @Override
    public MoonExpression getExpression() {
        return findChildByClass(MoonExpression.class);
    }

    @Override
    public IElementType getOperationTokenType() {
        return getOperator().getNode().getElementType();
    }

    @Override
    public MoonExpression getOperand() {
        return getExpression();
    }


    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitUnaryExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }

}
