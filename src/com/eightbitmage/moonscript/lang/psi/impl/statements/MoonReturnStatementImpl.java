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

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonReturnStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 10:42:37 PM
 */
public class MoonReturnStatementImpl extends MoonStatementElementImpl implements MoonReturnStatement {
    public MoonReturnStatementImpl(ASTNode node) {
        super(node);
    }


    @Override
    public MoonExpression getReturnValue() {
        return findChildByClass(MoonExpression.class);
    }

    @Override
    public boolean isTailCall() {
         return getNode().getElementType() == MoonElementTypes.RETURN_STATEMENT_WITH_TAIL_CALL;
    }


        @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitReturnStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitReturnStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

}
