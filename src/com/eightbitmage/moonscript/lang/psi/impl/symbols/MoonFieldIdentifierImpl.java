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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementFactoryImpl;import com.eightbitmage.moonscript.lang.psi.impl.MoonStubElementBase;import com.eightbitmage.moonscript.lang.psi.stubs.impl.MoonFieldStub;import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/11
 * Time: 1:31 AM
 */
public class MoonFieldIdentifierImpl extends MoonStubElementBase<MoonFieldStub> implements MoonFieldIdentifier {
    public MoonFieldIdentifierImpl(ASTNode node) {
        super(node);
    }

    public MoonFieldIdentifierImpl(MoonFieldStub stub) {
        super(stub, MoonElementTypes.FIELD_NAME);
    }

    @Override
    public PsiElement getParent() {
        return getDefinitionParent();
    }

    @Override
    public String getName() {
        return getText();   
    }

    @Override
    public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
        MoonIdentifier node = MoonPsiElementFactoryImpl.getInstance(getProject()).createFieldNameIdentifier(name);
        replace(node);

        return this;
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean removeUnnecessaryParentheses) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }
    
    @Override
    public boolean isSameKind(MoonSymbol identifier) {
        return identifier instanceof MoonFieldIdentifier;
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

    public boolean  isDeclaration() {
        return isAssignedTo();
    }

    @Override
    public boolean isAssignedTo() {
        MoonCompoundIdentifier v = getCompositeIdentifier();

        if (v == null)
            return true; // the only times fields are not part of a composite identifier are table constructors.

        return false;//v.isAssignedTo();
    }

    public MoonCompoundIdentifier getCompositeIdentifier() {
        if (getParent() instanceof MoonCompoundIdentifier)
            return ((MoonCompoundIdentifier) getParent()).getEnclosingIdentifier();

        return null;
    }
    @Override
    public String toString() {
        return "Field: " + getText();
    }


    @Override
    public MoonCompoundIdentifier getEnclosingIdentifier() {
        return getCompositeIdentifier();
    }


    public PsiElement getNameIdentifier() {
        return this;
    }
}
