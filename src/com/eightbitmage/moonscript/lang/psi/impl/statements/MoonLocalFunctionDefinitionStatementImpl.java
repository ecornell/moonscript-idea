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
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 10/22/10
 * Time: 1:41 AM
 */
public class MoonLocalFunctionDefinitionStatementImpl extends MoonFunctionDefinitionStatementImpl {
    public MoonLocalFunctionDefinitionStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getIdentifier().getName();
    }

    public MoonSymbol getIdentifier() {
        return findChildByClass(MoonSymbol.class);
    }

    public MoonDeclarationExpression getDeclaration() {
        return (MoonDeclarationExpression) getIdentifier();
    }

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState,
                                       PsiElement lastParent, @NotNull PsiElement place) {

        MoonSymbol v = getIdentifier();
        if (v != null) if (!processor.execute(v, resolveState)) return false;

        PsiElement parent = place.getParent();
        while (parent != null && !(parent instanceof MoonPsiFile)) {
            if (parent == getBlock()) {
                final MoonParameter[] params = getParameters().getLuaParameters();
                for (MoonParameter param : params) {
                    if (!processor.execute(param, resolveState)) return false;
                }

                break;
            }

            parent = parent.getParent();
        }

        return super.processDeclarations(processor, resolveState, lastParent, place);
    }
}
