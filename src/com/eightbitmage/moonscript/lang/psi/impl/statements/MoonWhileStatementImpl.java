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

import com.eightbitmage.moonscript.lang.psi.PsiMoonToken;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonConditionalExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonWhileStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class MoonWhileStatementImpl extends MoonStatementElementImpl implements MoonWhileStatement {

    public MoonWhileStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitWhileStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitWhileStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonConditionalExpression getCondition() {
        return findChildByClass(MoonConditionalExpression.class);
    }

    @Override
    public PsiMoonToken getLParenth() {
        return null;
    }

    @Override
    public PsiMoonToken getRParenth() {
        return null;
    }

    @Override
    public MoonBlock getBody() {
        return findChildByClass(MoonBlock.class);
    }


}
