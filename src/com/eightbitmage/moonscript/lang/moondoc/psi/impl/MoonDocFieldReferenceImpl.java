/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.eightbitmage.moonscript.lang.moondoc.psi.impl;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocFieldReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTagValueToken;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResultImpl;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonKeyValueInitializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


public class MoonDocFieldReferenceImpl extends MoonDocReferenceElementImpl implements MoonDocFieldReference {

    public MoonDocFieldReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
        return "MoonDocFieldReference: " + StringUtil.notNullize(getName());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @NotNull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final String name = getName();
        if (name == null) return ResolveResult.EMPTY_ARRAY;
        ArrayList<MoonResolveResult> candidates = new ArrayList<MoonResolveResult>();

        final PsiElement owner = MoonDocCommentUtil.findDocOwner(this);
        if (owner instanceof MoonTableConstructor) {
            MoonExpression[] inits = ((MoonTableConstructor) owner).getInitializers();

            for (MoonExpression expr : inits) {
                if (expr instanceof MoonKeyValueInitializer) {
                    MoonFieldIdentifier fieldKey = (MoonFieldIdentifier) ((MoonKeyValueInitializer) expr).getFieldKey();
                    if (fieldKey.getName().equals(getName())) candidates.add(new MoonResolveResultImpl(fieldKey,
                            true));
                }
            }

            return candidates.toArray(new ResolveResult[candidates.size()]);
        }

        return ResolveResult.EMPTY_ARRAY;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    @Override
    public String getName() {
        return getReferenceNameElement().getText();
    }

    @Nullable
    public PsiElement resolve() {
        final ResolveResult[] results = multiResolve(false);
        if (results.length != 1) return null;
        return results[0].getElement();
    }

    @NotNull
    public String getCanonicalText() {
        return StringUtil.notNullize(getName());
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        PsiElement nameElement = getReferenceNameElement();
        ASTNode node = nameElement.getNode();
        ASTNode newNameNode =
                MoonPsiElementFactory.getInstance(getProject()).createDocFieldReferenceNameFromText(newElementName)
                        .getNode();
        assert newNameNode != null && node != null;
        node.getTreeParent().replaceChild(node, newNameNode);
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element)) return this;
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        if (!(element instanceof MoonFieldIdentifier)) return false;
        return getManager().areElementsEquivalent(element, resolve());
    }

    @NotNull
    public Object[] getVariants() {
        final PsiElement owner = MoonDocCommentUtil.findDocOwner(this);

        ArrayList<Object> candidates = new ArrayList<Object>();
        if (owner instanceof MoonTableConstructor) {
            MoonExpression[] inits = ((MoonTableConstructor) owner).getInitializers();

            for (MoonExpression expr : inits) {
                if (expr instanceof MoonKeyValueInitializer)
                    candidates.add(((MoonKeyValueInitializer) expr).getFieldKey());
            }

            return candidates.toArray();
        }

        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    public boolean isSoft() {
        return false;
    }

    @Nullable
    public MoonDocTagValueToken getReferenceNameElement() {
        MoonDocTagValueToken token = findChildByClass(MoonDocTagValueToken.class);
        return token;
    }
}
