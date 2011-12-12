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

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.impl.MoonPsiElementFactoryImpl;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 1/24/11
 * Time: 12:26 AM
 */
public class MoonGlobalUsageImpl extends MoonIdentifierImpl implements MoonGlobalIdentifier {
    public MoonGlobalUsageImpl(ASTNode node) {
        super(node);
    }

    @Override
    public boolean isAssignedTo() {
        return false;
    }

    @Override
    public boolean isSameKind(MoonSymbol identifier) {
        return identifier instanceof MoonGlobalDeclaration;
    }

    @Override
    public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
        MoonIdentifier node = MoonPsiElementFactoryImpl.getInstance(getProject()).createGlobalNameIdentifier(name);
        replace(node);

        return this;
    }

    @Override
    public String getName() {
        if (isValid()) {
            String module = getModuleName();
            if (module != null) return module + "." + super.getName();
        }
        
        return super.getName();
    }

    @Override
    public PsiReference getReference() {
        return (PsiReference) getParent();
    }

    //    @Override
//    public boolean isEquivalentTo(PsiElement another) {
//        if (super.isEquivalentTo(another)) return true;
//        PsiElement element1 = this;
//        PsiElement element2 = another;
//
//        if (element1.getText().equals(element2.getText())) return true;
//    }


    @Override
    @Nullable
    public String getModuleName() {
        MoonPsiFile file = (MoonPsiFile) getContainingFile();
        if (file == null) return null;

        return file.getModuleNameAtOffset(getTextOffset());
    }

    @Override
    public String toString() {
        return "Global: " + getName();
    }
}
