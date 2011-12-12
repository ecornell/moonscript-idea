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

package com.eightbitmage.moonscript.lang.luadoc.psi.impl;

import com.eightbitmage.moonscript.lang.luadoc.lexer.LuaDocTokenTypes;
import com.eightbitmage.moonscript.lang.luadoc.parser.LuaDocElementTypes;
import com.eightbitmage.moonscript.lang.psi.LuaPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.util.LuaPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.LuaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.luadoc.psi.api.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author ilyas
 */
public class LuaDocTagImpl extends LuaDocPsiElementImpl implements LuaDocTag {
    private static final TokenSet VALUE_BIT_SET =
            TokenSet.create(LuaDocTokenTypes.LDOC_TAG_VALUE, LuaDocElementTypes.LDOC_FIELD_REF, LuaDocElementTypes.LDOC_PARAM_REF, LuaDocElementTypes.LDOC_REFERENCE_ELEMENT,
                    LuaDocTokenTypes.LDOC_COMMENT_DATA);

    public LuaDocTagImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(LuaElementVisitor visitor) {
        visitor.visitDocTag(this);
    }

    public String toString() {
        return "LuaDocTag";
    }

    @NotNull
    public String getName() {
        return getNameElement().getText();
    }

    @NotNull
    public PsiElement getNameElement() {
        PsiElement element = findChildByType(LuaDocTokenTypes.LDOC_TAG_NAME);
        assert element != null;
        return element;
    }


    public LuaDocComment getContainingComment() {
        return (LuaDocComment) getParent();
    }

    public LuaDocTagValueToken getValueElement() {
        final LuaDocReferenceElement reference = findChildByClass(LuaDocReferenceElement.class);
        if (reference == null) return null;
        return reference.getReferenceNameElement();
    }

    @Nullable
    public LuaDocParameterReference getDocParameterReference() {
        return findChildByClass(LuaDocParameterReference.class);
    }

    @Override
    public LuaDocFieldReference getDocFieldReference() {
        return findChildByClass(LuaDocFieldReference.class);
    }

    @NotNull
    @Override
    public PsiElement[] getDescriptionElements() {
        final List<PsiElement> list = findChildrenByType(LuaDocTokenTypes.LDOC_COMMENT_DATA);
        return LuaPsiUtils.toPsiElementArray(list);
    }

    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        final PsiElement nameElement = getNameElement();
        final LuaPsiElementFactory factory = LuaPsiElementFactory.getInstance(getProject());
        final LuaDocComment comment = factory.createDocCommentFromText("--- @" + name);
        nameElement.replace(comment.getTags()[0].getNameElement());
        return this;
    }

}
