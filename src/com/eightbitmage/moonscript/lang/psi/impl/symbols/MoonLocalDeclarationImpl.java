/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
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

import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementFactoryImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocal;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocalDeclaration;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 3, 2010
 * Time: 12:38:19 AM
 */
public class MoonLocalDeclarationImpl extends MoonPsiDeclarationReferenceElementImpl
        implements MoonDeclarationExpression, MoonLocalDeclaration {
    public MoonLocalDeclarationImpl(ASTNode node) {
        super(node);
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
    public String getDefinedName() {
        return getName();
    }


    @Override
    public PsiElement setName(@NotNull String s) {
        MoonDeclarationExpression decl = MoonPsiElementFactoryImpl.getInstance(getProject()).createLocalNameIdentifierDecl(s);

        return replace(decl);
    }

    @Override
    public String toString() {
        return "Local Decl: " + getDefinedName();
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return GlobalSearchScope.fileScope(this.getContainingFile());
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }


    @Override
    public boolean isSameKind(MoonSymbol identifier) {
        return identifier instanceof MoonLocal;
    }

    PsiElement myAlias = null;

    @Override
    public PsiElement getAliasElement() {
        return myAlias;
    }

    @Override
    public void setAliasElement(PsiElement name) {
        myAlias = name;
    }
}
