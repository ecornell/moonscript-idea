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

import com.eightbitmage.moonscript.lang.lexer.MoonElementType;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonLiteralExpression;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static com.eightbitmage.moonscript.lang.lexer.MoonTokenTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 12:11:12 AM
 */
public class MoonLiteralExpressionImpl extends MoonExpressionImpl implements MoonLiteralExpression {
    public MoonLiteralExpressionImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Literal:" + getText();
    }

    @Override
    public Object getValue() {
        if (getLuaType() == MoonType.BOOLEAN) {
            if (getText().equals("false")) return false;
            if (getText().equals("true")) return true;
        }

        if (getLuaType() == MoonType.NUMBER) {
            try {
                return Double.parseDouble(getText());
            } catch (NumberFormatException unused) {
                return UNREPRESENTABLE_VALUE;
            }
        }

        if (getLuaType() == MoonType.NIL) return null;

        return UNREPRESENTABLE_VALUE;
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitLiteralExpression(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitLiteralExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }


    @Override
    public MoonType getLuaType() {
        PsiElement fc = getFirstChild();
        if (fc == null) return MoonType.ANY;

        MoonElementType e = (MoonElementType) fc.getNode().getElementType();

        if (e == FALSE || e == TRUE)
            return MoonType.BOOLEAN;

        if (e == NUMBER)
            return MoonType.NUMBER;

        if (e == STRING || e == LONGSTRING)
            return MoonType.STRING;

        if (e == NIL)
            return MoonType.NIL;

        return MoonType.ANY;
    }
}