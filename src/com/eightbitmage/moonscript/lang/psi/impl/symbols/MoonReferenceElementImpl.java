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
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolver;
import com.eightbitmage.moonscript.lang.psi.resolve.ResolveUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import com.intellij.openapi.diagnostic.Logger;


/**
 * TODO: implement all reference stuff...
 */
public abstract class MoonReferenceElementImpl extends MoonSymbolImpl implements MoonReferenceElement {

    private final static Logger LOG = Logger.getInstance(MoonReferenceElementImpl.class.getName());

    private static ResolveCache resolveCache = null;

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
        ResolveResult[] results = getResolveCache().resolveWithCaching(this, RESOLVER, true, false);
        return results.length == 1 ? results[0].getElement() : null;
    }

    @NotNull
    public ResolveResult[] multiResolve(final boolean incompleteCode) {
        return getResolveCache().resolveWithCaching(this, RESOLVER, true, incompleteCode);
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


    private ResolveCache getResolveCache() {
        if (null == resolveCache) {
            String appVersion = ApplicationInfo.getInstance().getMajorVersion();
            if ("11".equals(appVersion)) {
                // The IDEA 11 way: ResolveCache.getInstance(getProject());
                try {
                    Class resolveCacheClass = Class.forName("ResolveCache");
                    Method m = resolveCacheClass.getDeclaredMethod("getInstance", new Class[] { Project.class });
                    try {
                        resolveCache = (ResolveCache)m.invoke(null, getProject());
                    } catch (IllegalAccessException e) {
                        LOG.error("Could not access the 'getInstance' method on the ResolveCache class", e);
                    } catch (InvocationTargetException e) {
                        LOG.error("Could not invoke the 'getInstance' method on the ResolveCache class", e);
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error("Could not find the ResolveCache class!", e); // Now, this is a problem.
                } catch (NoSuchMethodException e) {
                    LOG.info("Could not resolve the ResolveCache#getInstance method.", e);
                }
            } else if ("10".equals(appVersion)) {
                // The IDEA 10 way: getManager().getResolveCache();
                Class managerClass = getManager().getClass();
                try {
                    Method m = managerClass.getDeclaredMethod("getResolveCache");
                    try {
                        resolveCache = (ResolveCache)m.invoke(managerClass);
                    } catch (IllegalAccessException e) {
                        LOG.error("Could not access the 'getResolveCache' method on the PsiManagerEx class", e);
                    } catch (InvocationTargetException e) {
                        LOG.error("Could not invoke the 'getResolveCache' method on the PsiManagerEx class", e);
                    }
                } catch (NoSuchMethodException e) {
                    LOG.error("Could not find the 'getResolveCache' method.", e);
                }
            }
        }
        return resolveCache;
    }
}