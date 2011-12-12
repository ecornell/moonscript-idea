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

import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonCompoundIdentifierStub;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonIdentifierList;
import com.eightbitmage.moonscript.lang.psi.impl.MoonStubElementBase;
import com.eightbitmage.moonscript.lang.psi.statements.MoonAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/20/11
 * Time: 3:44 AM
 */
public class MoonCompoundIdentifierImpl extends MoonStubElementBase<MoonCompoundIdentifierStub>
        implements MoonCompoundIdentifier {

    public MoonCompoundIdentifierImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getParent() {
         return SharedImplUtil.getParent(getNode());
    }

    public MoonCompoundIdentifierImpl(MoonCompoundIdentifierStub stub) {
        this(stub, MoonElementTypes.GETTABLE);
    }
    public MoonCompoundIdentifierImpl(MoonCompoundIdentifierStub stub, IStubElementType type) {
        super(stub, type);
    }


    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitCompoundIdentifier(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitCompoundIdentifier(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Nullable
    public MoonExpression getRightSymbol() {
        MoonExpression[] e = findChildrenByClass(MoonExpression.class);
        return e.length>1?e[1]:null;
    }

    @Nullable
    public MoonExpression getLeftSymbol() {
        MoonExpression[] e = findChildrenByClass(MoonExpression.class);
        return e.length>0?e[0]:null;
    }

    @Nullable
    @Override
    public String toString() {
        try {
        return "GetTable: " +  getLeftSymbol().getText() + getOperator() + getRightSymbol().getText();
        } catch (Throwable t) { return "err"; }
    }

    @Override
    public String getOperator() {
        try {
        return findChildByType(MoonElementTypes.TABLE_ACCESS).getText();
        } catch (Throwable t) { return "err"; }
    }

    @Override
    public MoonCompoundIdentifier getEnclosingIdentifier() {
        MoonCompoundIdentifier s = this;

        while (s.getParent() instanceof MoonCompoundIdentifier)
            s = (MoonCompoundIdentifier) getParent();

        return s;
    }

    @Override
    public boolean isCompoundDeclaration() {
        PsiElement e = getParent().getParent();
        return e instanceof MoonIdentifierList || e instanceof MoonFunctionDefinition;
    }


    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state, PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (isCompoundDeclaration()) {
            if (!processor.execute(this,state)) return false;
        }

        return MoonPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }

    @Override
    public PsiElement getScopeIdentifier() {
        PsiElement child = getFirstChild();

        if (child instanceof MoonCompoundReferenceElementImpl)
            child = ((MoonCompoundReferenceElementImpl) child).getElement();

        if (child instanceof MoonCompoundIdentifier)
            return ((MoonCompoundIdentifier) child).getScopeIdentifier();

        if (child instanceof MoonReferenceElement)
            return ((MoonReferenceElement) child).getElement();

        return null;
    }

    @Override
    public MoonFieldIdentifier getLeftMostField() {
        return findChildByClass(MoonFieldIdentifier.class);
    }

    @Override
    public boolean isSameKind(MoonSymbol symbol) {
        return symbol instanceof MoonCompoundIdentifier;
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



    @Override
    public String getDefinedName() {
        final MoonCompoundIdentifierStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        return super.getName();
    }

   @Override
    public String getName() {
        final MoonCompoundIdentifierStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        return getText();
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean removeUnnecessaryParentheses) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new NotImplementedException();
    }

    public PsiElement getNameIdentifier() {
        return this;
    }
}
