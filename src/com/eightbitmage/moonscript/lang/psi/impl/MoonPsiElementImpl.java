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

package com.eightbitmage.moonscript.lang.psi.impl;

import com.eightbitmage.moonscript.MoonFileType;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.ChangeUtil;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class MoonPsiElementImpl extends ASTWrapperPsiElement implements MoonPsiElement {

    public MoonPsiElementImpl(ASTNode node) {
        super(node);
    }

    protected void log(String text) {
        System.out.println(this+": "+text);
    }


    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitElement(this);
    }

    @Override
    public void acceptChildren(MoonElementVisitor visitor) {
        acceptLuaChildren(this, visitor);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitElement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public String toString() {
        return getNode().getElementType().toString();
    }

    @NotNull
    public Language getLanguage() {
        return MoonFileType.MOON_LANGUAGE;
    }

    @NotNull
    public SearchScope getUseScope() {
        //This is true as long as we have no inter-file references
        return GlobalSearchScope.allScope(getProject());
    }

//    @Override
//    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
//        return MoonPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
//    }

    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        CompositeElement treeElement = calcTreeElement();
        assert treeElement.getTreeParent() != null;
        CheckUtil.checkWritable(this);
        TreeElement elementCopy = ChangeUtil.copyToElement(newElement);
        treeElement.getTreeParent().replaceChildInternal(treeElement, elementCopy);
        elementCopy = ChangeUtil.decodeInformation(elementCopy);
        return SourceTreeToPsiMap.treeElementToPsi(elementCopy);
    }

    protected CompositeElement calcTreeElement() {
        return (CompositeElement) getNode();
    }


    public static void acceptLuaChildren(PsiElement parent, MoonElementVisitor visitor) {
        PsiElement child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof MoonPsiElement) {
                ((MoonPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }
}
