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

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonNumericForStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 13, 2010
 * Time: 2:11:33 AM
 */
public class MoonNumericForStatementImpl extends MoonStatementElementImpl implements MoonNumericForStatement {
    public MoonNumericForStatementImpl(ASTNode node) {
        super(node);
    }


    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        PsiElement parent = place.getParent();
        while (parent != null && !(parent instanceof MoonPsiFile)) {
            if (parent == getBody()) {
                if (!processor.execute(getIndex(), resolveState)) return false;
            }

            parent = parent.getParent();
        }

        return true;
    }

    @Override
    public MoonExpression getIndex() {
        return findChildrenByClass(MoonExpression.class)[0];
    }

    @Override
    public MoonExpression getStart() {
        return findChildrenByClass(MoonExpression.class)[1];
    }

    @Override
    public MoonExpression getEnd() {
        return findChildrenByClass(MoonExpression.class)[2];
    }

    @Override
    public MoonExpression getStep() {
        MoonExpression[] e = findChildrenByClass(MoonExpression.class);

        if (e.length >= 4)
            return e[3];

        return null;
    }

    @Override
    public MoonBlock getBody() {
        return findChildByClass(MoonBlock.class);
    }


    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitNumericForStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitNumericForStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }


}
