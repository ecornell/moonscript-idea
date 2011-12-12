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

import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFunctionArguments;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFunctionCallExpression;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Aug 28, 2010
 * Time: 10:04:11 AM
 */
public class MoonFunctionCallExpressionImpl extends MoonExpressionImpl implements MoonFunctionCallExpression, PsiNamedElement {
    public MoonFunctionCallExpressionImpl(ASTNode node) {
        super(node);
    }

    public String toString() {
        return "Call: " + getNameRaw();
    }

            @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitFunctionCall(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitFunctionCall(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public String getNameRaw() {
        MoonReferenceElement e = findChildByClass(MoonReferenceElement.class);

        if (e != null) return e.getText();

        return null;
    }

    @Override
    public String getName() {
        MoonReferenceElement e = findChildByClass(MoonReferenceElement.class);

        if (e != null) return e.getName();

        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @Nullable
    public MoonExpressionList getArgumentList() {
        return findChildByClass(MoonFunctionArguments.class).getExpressions();
    }

    @Override
    public MoonReferenceElement getFunctionNameElement() {
        return findChildByClass(MoonReferenceElement.class);
    }
}
