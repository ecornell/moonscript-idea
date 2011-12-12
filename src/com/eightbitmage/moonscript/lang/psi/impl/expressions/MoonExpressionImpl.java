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

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 11:38:37 PM
 */
public class MoonExpressionImpl extends MoonPsiElementImpl implements MoonExpression {
    public MoonExpressionImpl(ASTNode node) {
        super(node);
    }

    public String toString() {
        return getExpressionLabel() + ": " + getText().substring(0, Math.min(getText().length(), 10));
    }

    protected String getExpressionLabel() {
        return "Expr";
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean removeUnnecessaryParentheses) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }
}
