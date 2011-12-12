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

import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author ilyas
 */
public class MoonDocTagImpl extends MoonDocPsiElementImpl implements MoonDocTag {
    private static final TokenSet VALUE_BIT_SET =
            TokenSet.create(MoonDocTokenTypes.LDOC_TAG_VALUE, MoonDocElementTypes.LDOC_FIELD_REF, MoonDocElementTypes.LDOC_PARAM_REF, MoonDocElementTypes.LDOC_REFERENCE_ELEMENT,
                    MoonDocTokenTypes.LDOC_COMMENT_DATA);

    public MoonDocTagImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(MoonElementVisitor visitor) {
        visitor.visitDocTag(this);
    }

    public String toString() {
        return "MoonDocTag";
    }

    @NotNull
    public String getName() {
        return getNameElement().getText();
    }

    @NotNull
    public PsiElement getNameElement() {
        PsiElement element = findChildByType(MoonDocTokenTypes.LDOC_TAG_NAME);
        assert element != null;
        return element;
    }


    public MoonDocComment getContainingComment() {
        return (MoonDocComment) getParent();
    }

    public MoonDocTagValueToken getValueElement() {
        final MoonDocReferenceElement reference = findChildByClass(MoonDocReferenceElement.class);
        if (reference == null) return null;
        return reference.getReferenceNameElement();
    }

    @Nullable
    public MoonDocParameterReference getDocParameterReference() {
        return findChildByClass(MoonDocParameterReference.class);
    }

    @Override
    public MoonDocFieldReference getDocFieldReference() {
        return findChildByClass(MoonDocFieldReference.class);
    }

    @NotNull
    @Override
    public PsiElement[] getDescriptionElements() {
        final List<PsiElement> list = findChildrenByType(MoonDocTokenTypes.LDOC_COMMENT_DATA);
        return MoonPsiUtils.toPsiElementArray(list);
    }

    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        final PsiElement nameElement = getNameElement();
        final MoonPsiElementFactory factory = MoonPsiElementFactory.getInstance(getProject());
        final MoonDocComment comment = factory.createDocCommentFromText("--- @" + name);
        nameElement.replace(comment.getTags()[0].getNameElement());
        return this;
    }

}
