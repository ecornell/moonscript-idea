package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.psi.MoonPsiManager;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.resolve.completion.CompletionProcessor;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.eightbitmage.moonscript.options.MoonApplicationSettings;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolver;
import com.eightbitmage.moonscript.lang.psi.resolve.ResolveUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


/**
 * TODO: implement all reference stuff...
 */
public abstract class MoonReferenceElementImpl extends MoonSymbolImpl implements MoonReferenceElement {
    public MoonReferenceElementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitReferenceElement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitReferenceElement(this);
        } else {
            visitor.visitElement(this);
        }
    }


    public PsiElement getElement() {
        return this;
    }

    public PsiReference getReference() {
        return this;
    }


    public PsiElement getResolvedElement() {
        return resolve();
    }


    public TextRange getRangeInElement() {
        final PsiElement nameElement = getElement();
        return new TextRange(getTextOffset() - nameElement.getTextOffset(), nameElement.getTextLength());
    }

    @Nullable
    public PsiElement resolve() {
        ResolveResult[] results = getManager().getResolveCache().resolveWithCaching(this, RESOLVER, true, false);
        return results.length == 1 ? results[0].getElement() : null;
    }

    @NotNull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
        return getManager().getResolveCache().resolveWithCaching(this, RESOLVER, true, incompleteCode);
    }

    private static final MoonResolver RESOLVER = new MoonResolver();

    @NotNull
    public String getCanonicalText() {
        return getName();
    }

     public PsiElement setName(@NotNull String s) {
        ((PsiNamedElement)getElement()).setName(s);

        resolve();

        return this;
     }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        ((PsiNamedElement)getElement()).setName(newElementName);
        resolve();
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        findChildByClass(MoonIdentifier.class).replace(element);
        return this;
    }

    public boolean isReferenceTo(PsiElement element) {
        if (MoonApplicationSettings.getInstance().RESOLVE_ALIASED_IDENTIFIERS) {
            return resolve() == element;
        } else {
//            return  ((PsiNamedElement)getElement()).getName().equals(((PsiNamedElement)element).getName());
            return getElement().getManager().areElementsEquivalent(element, resolve());
        }
        //return false;
    }

    @NotNull
    public Object[] getVariants() {
        CompletionProcessor variantsProcessor = new CompletionProcessor(this);
        ResolveUtil.treeWalkUp(this, variantsProcessor);

        Collection<String> names = MoonPsiManager.getInstance(getProject()).getFilteredGlobalsCache();
        if (names == null)
            return variantsProcessor.getResultElements();

        for (PsiElement e : variantsProcessor.getResultElements()) {
            final String name = ((PsiNamedElement) e).getName();
            if (name != null)
                names.add(name);
        }

        return names.toArray();
    }




    public boolean isSoft() {
        return false;
    }

    public boolean isAssignedTo() {
        return false;
    }

    public PsiElement replaceWithExpression(MoonExpression newCall, boolean b) {
        return MoonPsiUtils.replaceElement(this, newCall);
    }

    @Override
    public String getName() {
        return ((PsiNamedElement)getElement()).getName();
    }

    @Override
    public PsiElement resolveWithoutCaching(boolean ingnoreAlias) {

        boolean save = RESOLVER.getIgnoreAliasing();
        RESOLVER.setIgnoreAliasing(ingnoreAlias);
        MoonResolveResult[] results = RESOLVER.resolve(this, false);
        RESOLVER.setIgnoreAliasing(save);

        if (results != null && results.length > 0)
            return results[0].getElement();

        return null;
    }
}