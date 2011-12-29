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
import com.eightbitmage.moonscript.lang.psi.controlFlow.Instruction;
import com.eightbitmage.moonscript.lang.psi.controlFlow.impl.ControlFlowBuilder;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonAnonymousFunctionExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonExpression;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonRecursiveElementVisitor;
import com.eightbitmage.moonscript.util.MoonModuleUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.PsiFileEx;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.IncorrectOperationException;
import com.eightbitmage.moonscript.lang.psi.*;
import com.eightbitmage.moonscript.lang.psi.statements.*;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonLocalIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoonPsiFileImpl
        extends MoonPsiFileBaseImpl
        implements MoonPsiFile, PsiFileWithStubSupport, PsiFileEx, MoonPsiFileBase, MoonExpressionCodeFragment {

    private boolean sdkFile;

    public MoonPsiFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, MoonFileType.MOON_LANGUAGE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MoonFileType.MOON_FILE_TYPE;
    }

    @Override
    public String toString() {
        return "Moon script: " + getName();
    }

    @Override
    public GlobalSearchScope getFileResolveScope() {
        return new ProjectAndLibrariesScope(getProject());
    }

    @Override
    public boolean ignoreReferencedElementAccessibility() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MoonStatementElement addStatementBefore(@NotNull MoonStatementElement statement, MoonStatementElement anchor) throws IncorrectOperationException {
        return (MoonStatementElement) addBefore(statement, anchor);
    }

    MoonModuleStatement[] moduleStatements;

    @Override @Nullable
    public String getModuleNameAtOffset(final int offset) {
        if (moduleStatements == null) {
            moduleStatements = findChildrenByClass(MoonModuleStatement.class);
        }

        if (moduleStatements.length == 0)
            return null;

        for (MoonModuleStatement m : moduleStatements) {
            if (m.getIncludedTextRange().contains(offset))
                return m.getName();
        }
        
        return null;
    }


    @Override
    public void clearCaches() {
        super.clearCaches();
        moduleStatements = null;
    }

    @Override
    public MoonExpression getReturnedValue() {
        // This only works for the last statement in the file
        MoonStatementElement[] stmts = getStatements();
        if (stmts.length==0) return null;

        MoonStatementElement s = stmts[stmts.length-1];
        if (! (s instanceof MoonReturnStatement)) return null;

        return ((MoonReturnStatement) s).getReturnValue();
    }


    @Override
    public boolean isSdkFile() {
        MoonModuleUtil.checkForSdkFile(this, getProject());
        return sdkFile;
    }

    @Override
    public void setSdkFile(boolean b) {
        sdkFile = b;
    }


    @Override
    public void removeVariable(MoonIdentifier variable) {

    }

    @Override
    public MoonDeclarationStatement addVariableDeclarationBefore(MoonDeclarationStatement declaration, MoonStatementElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        final PsiElement[] children = getChildren();
        for (PsiElement child : children) {
            if (child == lastParent) break;
            if (!child.processDeclarations(processor, resolveState, lastParent, place)) return false;
        }
        return true;
    }


    public void accept(MoonElementVisitor visitor) {
        visitor.visitFile(this);
    }

    public void acceptChildren(MoonElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof MoonPsiElement) {
                ((MoonPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }


    @Override
    public MoonDeclarationExpression[] getSymbolDefs() {
        final Set<MoonDeclarationExpression> decls =
                new HashSet<MoonDeclarationExpression>();

        MoonElementVisitor v = new MoonRecursiveElementVisitor() {
            public void visitDeclarationExpression(MoonDeclarationExpression e) {
                super.visitDeclarationExpression(e);
                if (!(e instanceof MoonLocalIdentifier))
                    decls.add(e);
            }

            @Override
            public void visitCompoundIdentifier(MoonCompoundIdentifier e) {
                super.visitCompoundIdentifier(e);

                if (e.isAssignedTo())
                    decls.add(e);
            }
        };

        v.visitElement(this);

        return decls.toArray(new MoonDeclarationExpression[decls.size()]);
    }


    @Override
    public MoonStatementElement[] getAllStatements() {
                final List<MoonStatementElement> stats =
                new ArrayList<MoonStatementElement>();

        MoonElementVisitor v = new MoonRecursiveElementVisitor() {
            public void visitElement(MoonPsiElement e) {
                super.visitElement(e);
                if (e instanceof MoonStatementElement)
                    stats.add((MoonStatementElement) e);
            }
        };

        v.visitElement(this);

        return stats.toArray(new MoonStatementElement[stats.size()]);
    }

    @Override
    public MoonStatementElement[] getStatements() {
         return findChildrenByClass(MoonStatementElement.class);
    }

    @Override
    public MoonFunctionDefinition[] getFunctionDefs() {
        final List<MoonFunctionDefinition> funcs =
                new ArrayList<MoonFunctionDefinition>();

        MoonElementVisitor v = new MoonRecursiveElementVisitor() {
            public void visitFunctionDef(MoonFunctionDefinitionStatement e) {
                super.visitFunctionDef(e);
                funcs.add(e);
            }

            @Override
            public void visitAnonymousFunction(MoonAnonymousFunctionExpression e) {
                super.visitAnonymousFunction(e);
                if (e.getName() != null)
                    funcs.add(e);
            }
        };

        v.visitElement(this);

        return funcs.toArray(new MoonFunctionDefinition[funcs.size()]);
    }

    @Override
    public Instruction[] getControlFlow() {
        assert isValid();
        CachedValue<Instruction[]> controlFlow = getUserData(CONTROL_FLOW);
        if (controlFlow == null) {
            controlFlow = CachedValuesManager.getManager(getProject()).createCachedValue(
                    new CachedValueProvider<Instruction[]>() {
                        @Override
                        public Result<Instruction[]> compute() {
                            return Result
                                    .create(new ControlFlowBuilder(getProject()).buildControlFlow(MoonPsiFileImpl.this),
                                            getContainingFile());
                        }
                    }, false);
            putUserData(CONTROL_FLOW, controlFlow);
        }

        return controlFlow.getValue();
    }

    // Only looks at the current block
    private class MoonModuleVisitor extends MoonElementVisitor {
        private final int offset;
        private String moduleName = null;

        public MoonModuleVisitor(int offset) {this.offset = offset;}

        public void visitModuleStatement(MoonModuleStatement e) {
            super.visitModuleStatement(e);
            if (e.getIncludedTextRange().contains(offset))
                moduleName = e.getName();
        }

        @Nullable
        public String getModuleName() {
            return moduleName;
        }
    }
}
