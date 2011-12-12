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
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementFactoryImpl;
import com.eightbitmage.moonscript.lang.psi.impl.MoonStubElementBase;
import com.eightbitmage.moonscript.lang.psi.stubs.api.MoonGlobalDeclarationStub;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/15/11
 * Time: 1:31 AM
 */
public class MoonGlobalDeclarationImpl extends MoonStubElementBase<MoonGlobalDeclarationStub>
        implements MoonGlobalDeclaration {
    public MoonGlobalDeclarationImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getParent() {
        return getDefinitionParent();
    }

    public MoonGlobalDeclarationImpl(MoonGlobalDeclarationStub stub) {
        super(stub, MoonElementTypes.GLOBAL_NAME_DECL);
    }

    @Override
    public String toString() {
        return "Global Decl: " + getName();
    }

    @Override
    public String getDefinedName() {
        return getText();
    }

    @Override @Nullable
    public String getModuleName() {
//        final MoonGlobalDeclarationStub stub = getStub();
//        if (stub != null) {
//            return stub.getModule();
//        }
        if (!isValid()) return null;
        
        MoonPsiFile file = (MoonPsiFile) getContainingFile();
        if (file == null)
            return null;
        
        return file.getModuleNameAtOffset(getTextOffset());
    }


    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state, PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return processor.execute(this, state);
    }


    @Override
    public String getName() {
        final MoonGlobalDeclarationStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        String module = getModuleName();
        if (module != null)
            return module + "." + super.getText();
        
        return super.getText();  
    }


    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitDeclarationExpression(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitDeclarationExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newExpr, boolean removeUnnecessaryParentheses) {
        return MoonPsiUtils.replaceElement(this, newExpr);
    }

    @Override
    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        MoonDeclarationExpression decl = MoonPsiElementFactoryImpl.getInstance(getProject()).createGlobalNameIdentifierDecl(name);

        return replace(decl);
    }

    @NotNull
    @Override
    public IStubElementType getElementType() {
        return MoonElementTypes.GLOBAL_NAME_DECL;
    }

    @Override
    public boolean isSameKind(MoonSymbol symbol) {
        return symbol instanceof MoonGlobalIdentifier;
    }

    @Override
    public boolean isAssignedTo() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.ANY;
    }


    public PsiElement getNameIdentifier() {
        return this;
    }
}
