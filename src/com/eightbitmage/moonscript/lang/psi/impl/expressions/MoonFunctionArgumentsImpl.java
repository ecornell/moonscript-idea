/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.lang.psi.impl.expressions;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpressionList;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementImpl;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFunctionArguments;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/17/11
 * Time: 12:40 AM
 */
public class MoonFunctionArgumentsImpl extends MoonPsiElementImpl implements MoonFunctionArguments {
    public MoonFunctionArgumentsImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newCall, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }

    @Override
    public MoonExpressionList getExpressions() {
        return findChildByClass(MoonExpressionList.class);
    }
}
