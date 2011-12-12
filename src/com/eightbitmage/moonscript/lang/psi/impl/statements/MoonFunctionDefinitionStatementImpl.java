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

package com.eightbitmage.moonscript.lang.psi.impl.statements;

import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.impl.MoonDocCommentUtil;
import com.eightbitmage.moonscript.lang.parser.MoonElementTypes;
import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.MoonReferenceElement;
import com.eightbitmage.moonscript.lang.psi.expressions.MoonParameterList;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonCompoundIdentifierImpl;
import com.eightbitmage.moonscript.lang.psi.impl.symbols.MoonImpliedSelfParameterImpl;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonGlobalDeclaration;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonParameter;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 10, 2010
 * Time: 10:40:55 AM
 */
public class MoonFunctionDefinitionStatementImpl extends MoonStatementElementImpl implements MoonFunctionDefinitionStatement/*, PsiModifierList */ {
    private boolean definesSelf = false;

    public MoonFunctionDefinitionStatementImpl(ASTNode node) {
        super(node);

        assert getBlock() != null;
    }

    public void accept(MoonElementVisitor visitor) {
        visitor.visitFunctionDef(this);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MoonElementVisitor) {
            ((MoonElementVisitor) visitor).visitFunctionDef(this);
        } else {
            visitor.visitElement(this);
        }
    }


    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState, PsiElement lastParent, @NotNull PsiElement place) {

        MoonSymbol v = getIdentifier();
        if (v != null && (v instanceof MoonGlobalDeclaration || (v instanceof MoonCompoundIdentifierImpl && ((MoonCompoundIdentifierImpl) v).isCompoundDeclaration())))
            if (!processor.execute(v, resolveState)) return false;

        PsiElement parent = place.getParent();
        while (parent != null && !(parent instanceof MoonPsiFile)) {
            if (parent == getBlock()) {
                final MoonParameter[] params = getParameters().getLuaParameters();
                for (MoonParameter param : params) {
                    if (!processor.execute(param, resolveState)) return false;
                }
                MoonParameter self = findChildByClass(MoonImpliedSelfParameterImpl.class);

                if (self != null) {
                    if (!processor.execute(self, resolveState)) return false;
                }

            }

            parent = parent.getParent();
        }


//
//        if (!getBlock().processDeclarations(processor, resolveState, lastParent, place))
//            return false;

//        if (getIdentifier() == null || !getIdentifier().isLocal())
//            return true;


        return true;
    }


    @Nullable
    @NonNls
    public String getName() {
        MoonSymbol name = getIdentifier();

        return name != null ? name.getName() : "anonymous";
    }

    @Override
    public PsiElement setName(String s) {
        return null;//getIdentifier().setName(s);
    }


    @Override
    public MoonSymbol getIdentifier() {
        MoonReferenceElement e = findChildByClass(MoonReferenceElement.class);
        if (e != null) {
            return (MoonSymbol) e.getElement();
        }
        return null;
    }

    @Override
    public String getDocString() {
        return null;
    }

    @Override
    public String getParameterString() {
        return getParameters().getText();
    }

    @Override
    public MoonParameterList getParameters() {
        PsiElement e = findChildByType(MoonElementTypes.PARAMETER_LIST);
        if (e != null) return (MoonParameterList) e;

        return null;
    }

    public MoonBlock getBlock() {
        PsiElement e = findChildByType(MoonElementTypes.BLOCK);
        if (e != null) return (MoonBlock) e;
        return null;
    }


    @Override
    public String toString() {
        return "Function Declaration (" + (getIdentifier() != null ? getIdentifier().getName() : "null") + ")";
    }


    @Override
    public MoonDocComment getDocComment() {
        return MoonDocCommentUtil.findDocComment(this);
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }
}
