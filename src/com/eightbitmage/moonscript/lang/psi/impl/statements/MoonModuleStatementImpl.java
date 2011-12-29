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

package com.eightbitmage.moonscript.lang.psi.impl.statements;

import com.eightbitmage.moonscript.lang.psi.expressions.*;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolveResult;
import com.eightbitmage.moonscript.lang.psi.resolve.MoonResolver;
import com.eightbitmage.moonscript.lang.psi.statements.MoonModuleStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.types.MoonType;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonLiteralExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalIdentifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 3/7/11
 * Time: 11:21 AM
 */
public class MoonModuleStatementImpl extends MoonFunctionCallStatementImpl implements MoonModuleStatement {

    private final static Logger LOG = Logger.getInstance(MoonModuleStatementImpl.class.getName());

    private static ResolveCache resolveCache = null;

    public MoonModuleStatementImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(MoonElementVisitor visitor) {
        visitor.visitModuleStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitModuleStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String toString() {
        String name = getName();
        return "Module: " + (name !=null? name :"err");
    }

    PsiElement getNameElement() {
        MoonFunctionCallExpression invoked = getInvokedExpression();
        if (invoked == null) return null;

        MoonExpressionList args = invoked.getArgumentList();
        if (args == null) return null;

        return args.getMoonExpressions().get(0);
    }

    public String getName() {
        MoonFunctionCallExpression invoked = getInvokedExpression();
        if (invoked == null) return null;

        MoonExpressionList args = invoked.getArgumentList();
        if (args == null) return null;
        
        MoonExpression expression = args.getMoonExpressions().get(0);

        MoonLiteralExpression lit = null;

        if (expression instanceof MoonLiteralExpression)
            lit = (MoonLiteralExpression) expression;

        if (lit != null && lit.getLuaType() == MoonType.STRING) {
            return (String) lit.getValue();
        }

        if (expression instanceof MoonSymbol && StringUtil.notNullize(((MoonSymbol) expression).getName()).equals("..."
        )) {
            final VirtualFile virtualFile = getContainingFile().getVirtualFile();
            if (virtualFile != null) {
                return virtualFile.getNameWithoutExtension();
            }
        }
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null; 
    }

    @Override
    public String getDefinedName() {
        return getName();
    }

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        processor.execute(this, resolveState);

        return true;
    }


    @Override
    public boolean isSameKind(MoonSymbol symbol) {
        return symbol instanceof MoonGlobalIdentifier || symbol instanceof MoonCompoundIdentifier;
    }

    @Override
    public boolean isAssignedTo() {
        return true; 
    }

    @Override
    public PsiElement replaceWithExpression(MoonExpression newCall, boolean b) {
        return null;
    }

    @Override
    public MoonType getLuaType() {
        return MoonType.TABLE;
    }

    @Override
    public TextRange getIncludedTextRange() {
        return new TextRange(getTextOffset()+getTextLength(), getContainingFile().getTextRange().getEndOffset());
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

    @Override
    public PsiElement getElement() {
        return getNameElement();
    }

    @NotNull
    public TextRange getRangeInElement() {
        PsiElement e = getElement();
        if (e != null)
            return e.getTextRange();

        return TextRange.EMPTY_RANGE;
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
        return getText();
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiElement getNameIdentifier() {
        return this;
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
