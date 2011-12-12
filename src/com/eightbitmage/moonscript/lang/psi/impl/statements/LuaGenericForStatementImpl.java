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

import com.eightbitmage.moonscript.lang.psi.LuaPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaExpression;
import com.eightbitmage.moonscript.lang.psi.statements.LuaBlock;
import com.eightbitmage.moonscript.lang.psi.statements.LuaGenericForStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.LuaElementVisitor;
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
 * Time: 2:12:45 AM
 */
public class LuaGenericForStatementImpl extends LuaStatementElementImpl implements LuaGenericForStatement {
    public LuaGenericForStatementImpl(ASTNode node) {
        super(node);
    }


    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        PsiElement parent = place.getParent();
        while(parent != null && !(parent instanceof LuaPsiFile)) {
            if (parent == getBody() ) {
                LuaExpression[] names = getIndices();
                for (LuaExpression name : names) {
                     if (!processor.execute(name, resolveState)) return false;
                }
            }

            parent = parent.getParent();
        }
       return true;
    }

    @Override
    public LuaExpression[] getIndices() {
        return findChildrenByClass(LuaDeclarationExpression.class);
    }

    @Override
    public LuaExpression getInClause() {
        return findChildrenByClass(LuaExpression.class)[0];
    }

    @Override
    public LuaBlock getBody() {
        return findChildByClass(LuaBlock.class);
    }

        @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitGenericForStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitGenericForStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

}
