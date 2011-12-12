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

import com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonBinaryExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 11:37:52 PM
 */
public class MoonBinaryExpressionImpl extends MoonExpressionImpl implements MoonBinaryExpression {
    public MoonBinaryExpressionImpl(ASTNode node) {
        super(node);
    }

    @Override
    public MoonPsiElement getOperator() {
        return (MoonPsiElement) findChildByType(MoonElementTypes.BINARY_OP);
    }

    @Override
    public String toString() {
        try {
        return super.toString() + " ("  + getLeftExpression().getText() + ") " + getOperator().getText() + " (" + getRightExpression().getText() +  ")";
        } catch (Throwable unused) {}

        return "err";
    }

    @Override
    public MoonExpression getLeftExpression() {
        MoonExpression[] e = findChildrenByClass(MoonExpression.class);
        return  e.length>0?e[0]:null;
    }

    @Override
    public IElementType getOperationTokenType() {
        ASTNode child = getOperator().getNode().findChildByType(MoonTokenTypes.BINARY_OP_SET);
        return child!=null ? child.getElementType() : null;
    }

    @Override
    public MoonExpression getLeftOperand() {
        return getLeftExpression();
    }

    @Override
    public MoonExpression getRightOperand() {
        return getRightExpression();
    }

    @Override
    public MoonExpression getRightExpression() {
        MoonExpression[] e = findChildrenByClass(MoonExpression.class);
        return  e.length>1?e[1]:null;
    }
        @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitBinaryExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
