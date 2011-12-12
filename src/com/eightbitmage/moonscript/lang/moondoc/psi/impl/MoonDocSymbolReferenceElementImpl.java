/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.moondoc.psi.impl;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocSymbolReference;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocCommentOwner;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTagValueToken;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonFieldIdentifier;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.processors.ResolveProcessor;
import com.eightbitmage.moonscript.lang.psi.resolve.processors.SymbolResolveProcessor;
import com.eightbitmage.moonscript.lang.psi.statements.MoonStatementElement;
import com.eightbitmage.moonscript.lang.psi.stubs.index.MoonGlobalDeclarationIndex;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 5/25/11
 * Time: 5:20 PM
 */
public class MoonDocSymbolReferenceElementImpl extends MoonDocReferenceElementImpl implements MoonDocSymbolReference {
    public MoonDocSymbolReferenceElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
        return "MoonDocSymbolReference: " + StringUtil.notNullize(getName());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @NotNull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
        final String refName = getName();
        if (refName == null)
            return MoonResolveResult.EMPTY_ARRAY;

        ResolveProcessor processor = new SymbolResolveProcessor(refName, this, incompleteCode);

        final MoonDocCommentOwner docOwner = MoonDocCommentUtil.findDocOwner(this);
        if (docOwner != null) {
            final MoonStatementElement statementElement =
                    PsiTreeUtil.getParentOfType(docOwner, MoonStatementElement.class, false);
            if (statementElement != null)
                statementElement.processDeclarations(processor, ResolveState.initial(), this, this);
        }
        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }
        
        MoonGlobalDeclarationIndex index = MoonGlobalDeclarationIndex.getInstance();
        Collection<MoonDeclarationExpression> names = index.get(refName, getProject(),
                new ProjectAndLibrariesScope(getProject()));
        for (MoonDeclarationExpression name : names) {
            name.processDeclarations(processor, ResolveState.initial(), this, this);
        }

        if (processor.hasCandidates()) {
            return processor.getCandidates();
        }

        return MoonResolveResult.EMPTY_ARRAY;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("not implemented");
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

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException("not implemented");
    }

    @Override
    public MoonDocTagValueToken getReferenceNameElement() {
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
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false; 
    }
}
