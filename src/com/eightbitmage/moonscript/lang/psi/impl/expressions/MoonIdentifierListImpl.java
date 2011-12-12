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
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 13, 2010
 * Time: 8:16:33 AM
 */
public class MoonIdentifierListImpl extends MoonExpressionImpl implements MoonIdentifierList {
    public MoonIdentifierListImpl(ASTNode node) {
        super(node);
    }

    @Override
    public int count() {
        return findChildrenByClass(MoonSymbol.class).length;
    }

    @Override
    public MoonSymbol[] getSymbols() {
        return findChildrenByClass(MoonSymbol.class);
    }


    @Override
    public MoonDeclarationExpression[] getDeclarations() {
        return findChildrenByClass(MoonDeclarationExpression.class);
    }

    public String toString() {
        return "Identifier List (Count " + count() + ")";
    }

    @Override
    public MoonReferenceElement[] getReferenceExprs() {
        return findChildrenByClass(MoonReferenceElement.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return MoonPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }



    @Override
    public PsiElement addAfter(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        if (getSymbols().length == 0) {
            add(element);
        } else {
            element = super.addAfter(element, anchor);
            final ASTNode astNode = getNode();
            if (anchor != null) {
                astNode.addLeaf(MoonTokenTypes.COMMA, ",", element.getNode());
            } else {
                astNode.addLeaf(MoonTokenTypes.COMMA, ",", element.getNextSibling().getNode());
            }
            CodeStyleManager.getInstance(getManager().getProject()).reformat(this);
        }

        return element;
    }
}
