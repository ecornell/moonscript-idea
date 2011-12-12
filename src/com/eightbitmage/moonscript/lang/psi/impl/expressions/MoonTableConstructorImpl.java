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
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.impl.MoonDocCommentUtil;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonTableConstructor;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.util.MoonAssignment;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 16, 2010
 * Time: 10:43:51 PM
 */
public class MoonTableConstructorImpl extends MoonExpressionListImpl implements MoonTableConstructor {
    TokenSet BRACES = TokenSet.create(MoonTokenTypes.LCURLY, MoonTokenTypes.RCURLY);
    TokenSet INITS = TokenSet.create(MoonElementTypes.KEY_ASSIGNMENT, MoonElementTypes.IDX_ASSIGNMENT);
    
    public MoonTableConstructorImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Table Constructor (Field Count " + count() + ")";
    }

    public MoonExpression[] getInitializers() {
        return findChildrenByClass(MoonExpression.class);
    }

        @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitTableConstructor(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitTableConstructor(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonDocComment getDocComment() {
        return MoonDocCommentUtil.findDocComment(this);
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public String getName() {
        MoonExpressionList exprlist = PsiTreeUtil.getParentOfType(this, MoonExpressionList.class);
        if (exprlist == null) return null;

        PsiElement assignment = exprlist.getParent();

        if (assignment instanceof MoonAssignmentStatement)
            for(MoonAssignment assign : ((MoonAssignmentStatement) assignment).getAssignments())
                if (assign.getValue() == this)
                    return assign.getSymbol().getName();

        return null;
    }
}
