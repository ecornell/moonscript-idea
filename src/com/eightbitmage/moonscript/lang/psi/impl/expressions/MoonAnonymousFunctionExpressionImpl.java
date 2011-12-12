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

import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonLocalDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonAnonymousFunctionExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParameterList;
import org.jetbrains.annotations.NotNull;

import static com.eightbitmage.moonscript.lang.parser.MoonElementTypes.BLOCK;
import static com.eightbitmage.moonscript.lang.parser.MoonElementTypes.PARAMETER_LIST;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 4, 2010
 * Time: 7:44:04 AM
 */
public class MoonAnonymousFunctionExpressionImpl extends MoonExpressionImpl implements MoonAnonymousFunctionExpression {
    public MoonAnonymousFunctionExpressionImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitAnonymousFunction(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitAnonymousFunction(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonParameterList getParameters() {
        return (MoonParameterList) findChildByType(PARAMETER_LIST);
    }

    @Override
    public MoonBlock getBlock() {
        return (MoonBlock) findChildByType(BLOCK);
    }

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

       if (lastParent != null && lastParent.getParent() == this) {
         final MoonParameter[] params = getParameters().getLuaParameters();
         for (MoonParameter param : params) {
           if (!processor.execute(param, resolveState)) return false;
         }
       }

        return true;
    }


    @Override
    public String getName() {
        MoonExpressionList exprlist = PsiTreeUtil.getParentOfType(this, MoonExpressionList.class);
        if (exprlist == null) return null;

        int idx = exprlist.getMoonExpressions().indexOf(this);
        if (idx < 0) return null;

        PsiElement assignment = exprlist.getParent();

        MoonIdentifierList idlist = null;
        if (assignment instanceof MoonAssignmentStatement)
            idlist = ((MoonAssignmentStatement) assignment).getLeftExprs();

        if (assignment instanceof MoonLocalDefinitionStatement)
            idlist = ((MoonLocalDefinitionStatement) assignment).getLeftExprs();

        if (idlist != null && idlist.count() > idx)
            return idlist.getSymbols()[idx].getName();

        return null;
    }
}
