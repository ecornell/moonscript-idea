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

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocParameterReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTagValueToken;
import com.eightbitmage.moonscript.lang.psi.MoonFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElementFactory;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResultImpl;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public class MoonDocParameterReferenceImpl extends MoonDocReferenceElementImpl implements MoonDocParameterReference {

  public MoonDocParameterReferenceImpl(@NotNull ASTNode node) {
    super(node);
  }

  public String toString() {
    return "MoonDocParameterReference";
  }

  public PsiReference getReference() {
    return this;
  }

  @NotNull
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    final String name = getName();
    if (name == null) return ResolveResult.EMPTY_ARRAY;
    ArrayList<MoonResolveResult> candidates = new ArrayList<MoonResolveResult>();

    final PsiElement owner = MoonDocCommentUtil.findDocOwner(this);
    if (owner instanceof MoonFunctionDefinitionStatement) {
      final MoonFunctionDefinitionStatement method = (MoonFunctionDefinitionStatement)owner;
      final MoonParameter[] parameters = method.getParameters().getLuaParameters();

      for (MoonParameter parameter : parameters) {
        if (name.equals(parameter.getName())) {
          candidates.add(new MoonResolveResultImpl(parameter, true));
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
    ASTNode newNameNode = MoonPsiElementFactory.getInstance(getProject()).createParameterDocMemberReferenceNameFromText(newElementName).getNode();
    assert node != null;
    node.getTreeParent().replaceChild(node, newNameNode);
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) return this;
    return null;
  }

  public boolean isReferenceTo(PsiElement element) {
    if (!(element instanceof MoonParameter)) return false;
    return getManager().areElementsEquivalent(element, resolve());
  }

  @NotNull
  public Object[] getVariants() {
    final PsiElement owner = MoonDocCommentUtil.findDocOwner(this);
    if (owner instanceof MoonFunctionDefinition) {
      return ((MoonFunctionDefinition)owner).getParameters().getLuaParameters();
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
