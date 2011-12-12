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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//import com.eightbitmage.moonscript.lang.psi.expressions.LuaVariable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Apr 11, 2010
 * Time: 2:33:37 PM
 */
public abstract class MoonIdentifierImpl extends MoonSymbolImpl implements MoonIdentifier {

    public MoonIdentifierImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
      visitor.visitIdentifier(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitIdentifier(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Nullable
    @NonNls
    public String getName() {
      return getText();
    }

    @Override
    public String toString() {
        return "Identifier: " + getName();
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean b) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }




    @Override
    public boolean isAssignedTo() {
        // This should return true if this variable is being assigned to in the current statement
        // it will be used for example by the global identifier class to decide if it should resolve
        // as a declaration or not

        PsiElement parent = getParent();
        while (!(parent instanceof MoonStatementElement)) {
            parent = parent.getParent();
        }

        if (parent instanceof MoonAssignmentStatement) {
            MoonAssignmentStatement s = (MoonAssignmentStatement)parent;

            for (MoonSymbol e : s.getLeftExprs().getSymbols())
                if (e == getParent().getParent())
                    return true;
        }
        else if (parent instanceof MoonFunctionDefinitionStatement) {
            MoonFunctionDefinitionStatement s = (MoonFunctionDefinitionStatement)parent;

            if (s.getIdentifier() == getParent().getParent())
                return true;
        }


        return false;
    }

}
