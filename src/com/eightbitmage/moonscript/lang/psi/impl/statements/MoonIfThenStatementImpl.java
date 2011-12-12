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
import com.eightbitmage.moonscript.lang.psi.expressions.MoonConditionalExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonIfThenStatement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class MoonIfThenStatementImpl extends MoonStatementElementImpl implements MoonIfThenStatement {

    public MoonIfThenStatementImpl(ASTNode node) {
        super(node);
    }

    public void accept(MoonElementVisitor visitor) {
      visitor.visitIfThenStatement(this);
    }
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitIfThenStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public MoonExpression getIfCondition() {
        return findChildByClass(MoonConditionalExpression.class);
    }

    @Override
    public MoonExpression[] getElseIfConditions() {
        return findChildrenByClass(MoonConditionalExpression.class);
    }

    @Override
    public MoonBlock getIfBlock() {
        return findChildrenByClass(MoonBlock.class)[0];
    }

    @Override
    public MoonBlock[] getElseIfBlocks() {
        MoonBlock[] b = findChildrenByClass(MoonBlock.class);
        return Arrays.copyOfRange(b, 1, getElseBlock()==null? b.length : b.length-1);
    }

    @Override
    public MoonBlock getElseBlock() {
        if (findChildByType(MoonElementTypes.ELSE) != null) {
            MoonBlock[] b = findChildrenByClass(MoonBlock.class);
            return b[b.length-1];
        }
        return null;
    }
}
